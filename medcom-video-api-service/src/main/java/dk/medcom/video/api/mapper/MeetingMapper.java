package dk.medcom.video.api.mapper;

import dk.medcom.video.api.controller.MeetingController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.MeetingLabel;
import dk.medcom.video.api.dao.MeetingUser;
import dk.medcom.video.api.dto.MeetingDto;
import dk.medcom.video.api.dto.MeetingUserDto;
import org.springframework.hateoas.Link;

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MeetingMapper {
    public MeetingDto mapFromMeeting(Meeting meeting, String shortLinkBaseUrl) {
        var meetingDto = new MeetingDto();
        meetingDto.subject = meeting.getSubject();
        meetingDto.uuid = meeting.getUuid();

        MeetingUser meetingUser = meeting.getMeetingUser();
        MeetingUserDto meetingUserDto = new MeetingUserDto(meetingUser);

        MeetingUser organizedByUser = meeting.getOrganizedByUser();
        MeetingUserDto organizedByUserDto = new MeetingUserDto(organizedByUser);

        MeetingUser updatedByUser = meeting.getUpdatedByUser();
        MeetingUserDto updatedByUserDto = new MeetingUserDto(updatedByUser);

        meetingDto.createdBy = meetingUserDto;
        meetingDto.organizedBy = organizedByUserDto;
        meetingDto.updatedBy = updatedByUserDto;
        meetingDto.startTime = meeting.getStartTime();
        meetingDto.endTime = meeting.getEndTime();
        meetingDto.description = meeting.getDescription();
        meetingDto.projectCode = meeting.getProjectCode();
        meetingDto.createdTime = meeting.getCreatedTime();
        meetingDto.updatedTime = meeting.getUpdatedTime();
        meetingDto.setShortId(meeting.getShortId());
        meetingDto.setShortlink(shortLinkBaseUrl + meeting.getShortId());
        meetingDto.setShortlink(meetingDto.getShortLink());
        meetingDto.setExternalId(meeting.getExternalId());
        meetingDto.setGuestMicrophone(meeting.getGuestMicrophone());
        meetingDto.setGuestPinRequired(meeting.getGuestPinRequired());

        meetingDto.setLabels(meeting.getMeetingLabels().stream().map(MeetingLabel::getLabel).collect(Collectors.toList()));

        try {
            Link selfLink = linkTo(methodOn(MeetingController.class).getMeetingByUUID(meetingDto.uuid)).withRel("self");
            meetingDto.add(selfLink);
        } catch (RessourceNotFoundException | PermissionDeniedException e) {
            // Empty
        }

        return meetingDto;
    }

}
