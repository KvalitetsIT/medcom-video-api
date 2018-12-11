package dk.medcom.video.api.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.repository.MeetingRepository;


@Component
public class MeetingService {

	@Autowired
	MeetingRepository meetingRepository;
	
	@Autowired
	MeetingUserService meetingUserService;
	
	@Autowired
	SchedulingInfoService schedulingInfoService;
	
	@Autowired
	SchedulingStatusService schedulingStatusService;
	
	@Autowired
	OrganisationService organisationService;

	@Autowired
	UserContextService userService;
	
	public List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) throws PermissionDeniedException {
		UserRole userRole = userService.getUserContext().getUserRole();
		
		
		if (userRole == UserRole.USER) {
			return meetingRepository.findByOrganizedByAndStartTimeBetween(meetingUserService.getOrCreateCurrentMeetingUser(), fromStartTime, toStartTime);
		} else {
			return meetingRepository.findByOrganisationAndStartTimeBetween(organisationService.getUserOrganisation(), fromStartTime, toStartTime);	
		}
		
	}

	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		UserRole userRole = userService.getUserContext().getUserRole();
		Meeting meeting = meetingRepository.findOneByUuid(uuid);
		if (meeting == null) {
			throw new RessourceNotFoundException("meeting", "uuid");
		}
		if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
			throw new PermissionDeniedException();
		}
		
		if (userRole == UserRole.USER && !(meeting.getOrganizedByUser() == meetingUserService.getOrCreateCurrentMeetingUser())) {
			throw new PermissionDeniedException();
		} 
		
		return meeting;
	}

	public Meeting createMeeting(CreateMeetingDto createMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException  {
		UserRole userRole = userService.getUserContext().getUserRole();
		Meeting meeting = convert(createMeetingDto);
		meeting.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		
		if (createMeetingDto.getOrganizedByEmail() != null && createMeetingDto.getOrganizedByEmail() != null && userRole == UserRole.MEETING_PLANNER) {
			meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser(createMeetingDto.getOrganizedByEmail()));
		} else {
			meeting.setOrganizedByUser(meeting.getMeetingUser());
		}
			
		Calendar calendarNow = new GregorianCalendar();
		meeting.setCreatedTime(calendarNow.getTime());
//		meeting.setUpdatedTime(calendarNow.getTime());
//		meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());

		meeting = meetingRepository.save(meeting);
		if (meeting != null) {
			schedulingInfoService.createSchedulingInfo(meeting);
		}
		return meeting;
	}

	public Meeting convert(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotValidDataException {
		
		validateDate(createMeetingDto.getStartTime());
		validateDate(createMeetingDto.getEndTime());
		
		Meeting meeting = new Meeting();
		meeting.setSubject(createMeetingDto.getSubject());
		meeting.setUuid(UUID.randomUUID().toString());
		meeting.setOrganisation(organisationService.getUserOrganisation());
		meeting.setStartTime(createMeetingDto.getStartTime());
		meeting.setEndTime(createMeetingDto.getEndTime());
		meeting.setDescription(createMeetingDto.getDescription());
		meeting.setProjectCode(createMeetingDto.getProjectCode());
	
		return meeting;
	}
	
	public Meeting updateMeeting(String uuid, UpdateMeetingDto updateMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		UserRole userRole = userService.getUserContext().getUserRole();
		Meeting meeting = getMeetingByUuid(uuid);
				
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION) {
			throw new NotAcceptableException("Meeting must have status AWAITS_PROVISION (0) in order to be updated");
		}
		
		validateDate(updateMeetingDto.getStartTime());
		validateDate(updateMeetingDto.getEndTime());
		
		meeting.setSubject(updateMeetingDto.getSubject());
		meeting.setStartTime(updateMeetingDto.getStartTime());
		meeting.setEndTime(updateMeetingDto.getEndTime());
		meeting.setDescription(updateMeetingDto.getDescription());
		meeting.setProjectCode(updateMeetingDto.getProjectCode());
		
		if (updateMeetingDto.getOrganizedByEmail() != null && updateMeetingDto.getOrganizedByEmail() != null  && userRole == UserRole.MEETING_PLANNER) {
			meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser(updateMeetingDto.getOrganizedByEmail()));
		} else {
			meeting.setOrganizedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		}

		Calendar calendarNow = new GregorianCalendar();
		meeting.setUpdatedTime(calendarNow.getTime());
		meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
		
		meeting = meetingRepository.save(meeting);
		schedulingInfoService.updateSchedulingInfo(uuid, meeting.getStartTime());
				
		return meeting;
	}
	
	public void deleteMeeting(String uuid) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException {
		
		Meeting meeting = getMeetingByUuid(uuid);
		
		SchedulingInfo schedulingInfo = schedulingInfoService.getSchedulingInfoByUuid(uuid);
		if (schedulingInfo.getProvisionStatus() != ProvisionStatus.AWAITS_PROVISION) {
			throw new NotAcceptableException("Meeting must have status AWAITS_PROVISION (0) in order to be deleted");
		}
		
		schedulingInfoService.deleteSchedulingInfo(uuid);
		schedulingStatusService.deleteSchedulingStatus(meeting);
		meetingRepository.delete(meeting);

	}

	private void validateDate(Date date) throws PermissionDeniedException, NotValidDataException {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.YEAR) > 9999) {
			throw new NotValidDataException("Date format is wrong, year must only have 4 digits");
		}
	}
}
