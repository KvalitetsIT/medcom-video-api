package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.service.exception.*;
import dk.medcom.video.api.service.mapper.v2.MeetingMapper;
import dk.medcom.video.api.service.model.CreateMeetingModel;
import dk.medcom.video.api.service.model.MeetingModel;
import dk.medcom.video.api.service.model.PatchMeetingModel;
import dk.medcom.video.api.service.model.UpdateMeetingModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MeetingServiceV2Impl implements MeetingServiceV2 {
    private final Logger logger = LoggerFactory.getLogger(MeetingServiceV2Impl.class);
    private final MeetingService meetingService;
    private final String shortLinkBaseUrl;

    public MeetingServiceV2Impl(MeetingService meetingService, String shortLinkBaseUrl) {
        this.meetingService = meetingService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
    }

    @Override
    public List<MeetingModel> getMeetingsV2(OffsetDateTime fromStartTime, OffsetDateTime toStartTime) {
        logger.debug("Get meetings by start time, v2.");
        try {
            return meetingService.getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingByShortIdV2(String shortId) {
        logger.debug("Get meeting by short id, v2.");
        try {
            return MeetingModel.from(meetingService.getMeetingByShortId(shortId), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> getMeetingsBySubjectV2(String subject) {
        logger.debug("Get meetings by subject, v2.");
        try {
            return meetingService.getMeetingsBySubject(subject)
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> getMeetingsByOrganizedByV2(String organizedBy) {
        logger.debug("Get meetings by organized by, v2.");
        try {
            return meetingService.getMeetingsByOrganizedBy(organizedBy)
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> getMeetingsByUriWithDomainV2(String uriWithDomain) {
        logger.debug("Get meetings by uri with domain, v2.");
        try {
            return meetingService.getMeetingsByUriWithDomain(uriWithDomain)
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> searchMeetingsV2(String search, OffsetDateTime fromStartTime, OffsetDateTime toStartTime) {
        logger.debug("Search meetings, v2.");
        try {
            var fromStartTimeDate = fromStartTime != null ? Date.from(fromStartTime.toInstant()) : null;
            var toStartTimeDate = fromStartTime != null ? Date.from(toStartTime.toInstant()) : null;

            return meetingService.searchMeetings(search, fromStartTimeDate, toStartTimeDate)
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingsByUriWithDomainSingleV2(String uriWithDomain) {
        logger.debug("Get meeting by uri without domain, v2.");
        try {
            return MeetingModel.from(meetingService.getMeetingsByUriWithDomainSingle(uriWithDomain), shortLinkBaseUrl);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }

    @Override
    public MeetingModel getMeetingsByUriWithoutDomainV2(String uriWithoutDomain) {
        logger.debug("Get meetings by uri without domain, v2.");
        try {
            return MeetingModel.from(meetingService.getMeetingsByUriWithoutDomain(uriWithoutDomain), shortLinkBaseUrl);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }

    @Override
    public List<MeetingModel> getMeetingsByLabelV2(String label) {
        logger.debug("Get meetings by label, v2.");
        try {
            return meetingService.getMeetingsByLabel(label)
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl)).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingByUuidV2(UUID uuid) {
        logger.debug("Get meeting by uuid, v2.");
        try {
            return MeetingModel.from(meetingService.getMeetingByUuid(uuid.toString()), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel createMeetingV2(CreateMeetingModel createMeeting) {
        logger.debug("Create meeting, v2.");
        try {
            return MeetingModel.from(meetingService.createMeeting(MeetingMapper.modelToDto(createMeeting)), shortLinkBaseUrl);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        } catch (NotValidDataException e) {
            throw new NotValidDataExceptionV2(ExceptionMapper.fromNotValidData(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public MeetingModel updateMeetingV2(UUID uuid, UpdateMeetingModel updateMeeting) {
        logger.debug("Update meeting, v2.");
        try {
            return MeetingModel.from(meetingService.updateMeeting(uuid.toString(), MeetingMapper.modelToDto(updateMeeting)), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        } catch (NotValidDataException e) {
            throw new NotValidDataExceptionV2(ExceptionMapper.fromNotValidData(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public void deleteMeetingV2(UUID uuid) {
        logger.debug("Delete meeting, v2.");
        try {
            meetingService.deleteMeeting(uuid.toString());
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public MeetingModel patchMeetingV2(UUID uuid, PatchMeetingModel patchMeeting) {
        logger.debug("Patch meeting, v2.");
        try {
            return MeetingModel.from(meetingService.patchMeeting(uuid, MeetingMapper.modelToDto(patchMeeting)), shortLinkBaseUrl);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotValidDataException e) {
            throw new NotValidDataExceptionV2(ExceptionMapper.fromNotValidData(e.getErrorCode()), e.getErrorText());
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        }
    }

}
