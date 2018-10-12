package dk.medcom.video.api.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateMeetingDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.dto.UpdateMeetingDto;
import dk.medcom.video.api.dto.UpdateSchedulingInfoDto;
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
	OrganisationService organisationService;
	
	
	public List<Meeting> getMeetings(Date fromStartTime, Date toStartTime) throws PermissionDeniedException {
	
		return meetingRepository.findByOrganisationAndStartTimeBetween(organisationService.getUserOrganisation(), fromStartTime, toStartTime);
	}

	public Meeting getMeetingByUuid(String uuid) throws RessourceNotFoundException, PermissionDeniedException {
		Meeting meeting = meetingRepository.findOneByUuid(uuid);
		if (meeting == null) {
			throw new RessourceNotFoundException("meeting", "uuid");
		}
		if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
			throw new PermissionDeniedException();
		}
		return meeting;
	}

	public Meeting createMeeting(CreateMeetingDto createMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException  {
		Meeting meeting = convert(createMeetingDto);
		meeting.setMeetingUser(meetingUserService.getOrCreateCurrentMeetingUser());
		
		meeting = meetingRepository.save(meeting);
		if (meeting != null) {
			schedulingInfoService.createSchedulingInfo(meeting);
		}
		return meeting;
	}

	public Meeting convert(CreateMeetingDto createMeetingDto) throws PermissionDeniedException, NotValidDataException {
		
		validateDate(createMeetingDto.getStartTime());
		validateDate(createMeetingDto.getEndTime());
		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(createMeetingDto.getStartTime());
//		if (calendar.get(Calendar.YEAR) > 9999) {
//			throw new NotValidDataException("startTime format is wrong, year must only have 4 digits");
//		}
//		calendar.setTime(createMeetingDto.getEndTime());
//		if (calendar.get(Calendar.YEAR) > 9999) {
//			throw new NotValidDataException("endTime format is wrong, year must only have 4 digits");
//		}
		
		Meeting meeting = new Meeting();
		meeting.setSubject(createMeetingDto.getSubject());
		meeting.setUuid(UUID.randomUUID().toString());
		meeting.setOrganisation(organisationService.getUserOrganisation());
		meeting.setStartTime(createMeetingDto.getStartTime());
		meeting.setEndTime(createMeetingDto.getEndTime());
		meeting.setDescription(createMeetingDto.getDescription());
	
		return meeting;
	}
	
	public Meeting updateMeeting(String uuid, UpdateMeetingDto updateMeetingDto) throws RessourceNotFoundException, PermissionDeniedException, NotAcceptableException, NotValidDataException {
		
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
