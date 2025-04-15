package dk.medcom.video.api.mapper;

import dk.medcom.video.api.api.AdditionalInformationType;
import dk.medcom.video.api.controller.MeetingSearchController;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingLabel;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.api.MeetingDto;
import dk.medcom.video.api.api.MeetingUserDto;
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
        meetingDto.setAdditionalInformation(meeting.getMeetingAdditionalInfo().stream().map(x -> new AdditionalInformationType(x.getInfoKey(), x.getInfoValue())).toList());

        try {
            Link selfLink = linkTo(methodOn(MeetingSearchController.class).getMeetingByUUID(meetingDto.uuid)).withRel("self");
            meetingDto.add(selfLink);
        } catch (RessourceNotFoundException | PermissionDeniedException e) {
            // Empty
        }

        return meetingDto;
    }
}
