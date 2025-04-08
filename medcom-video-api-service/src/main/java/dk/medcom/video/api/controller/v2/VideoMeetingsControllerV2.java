package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.PerformanceLogger;
import dk.medcom.video.api.controller.v2.exception.*;
import dk.medcom.video.api.controller.v2.mapper.VideoMeetingMapper;
import dk.medcom.video.api.interceptor.Oauth;
import dk.medcom.video.api.service.MeetingServiceV2;
import dk.medcom.video.api.service.RetryOnException;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.model.MeetingModel;
import org.openapitools.api.VideoMeetingsV2Api;
import org.openapitools.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLTransactionRollbackException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class VideoMeetingsControllerV2 implements VideoMeetingsV2Api {
    private static final Logger logger = LoggerFactory.getLogger(VideoMeetingsControllerV2.class);

    private final String anyScope = "hasAnyAuthority('SCOPE_meeting-user','SCOPE_meeting-admin','SCOPE_meeting-provisioner','SCOPE_meeting-provisioner-user','SCOPE_meeting-planner','SCOPE_undefined')";
    private final String plannerProvisionerUserAdminUserScope = "hasAnyAuthority('SCOPE_meeting-planner','SCOPE_meeting-provisioner-user','SCOPE_meeting-admin','SCOPE_meeting-user')";

    private final MeetingServiceV2 meetingService;

    public VideoMeetingsControllerV2(MeetingServiceV2 meetingService) {
        this.meetingService = meetingService;
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<Meeting> v2MeetingsFindByUriWithDomainGet(String uri) {
        logger.debug("Enter GET meetings by uri with domain: {}, v2.", uri);
        try {
            var meetings = meetingService.getMeetingsByUriWithDomainSingleV2(uri);
            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<Meeting> v2MeetingsFindByUriWithoutDomainGet(String uri) {
        logger.debug("Enter GET meetings by uri without domain: {}, v2.", uri);
        try {
            var meetings = meetingService.getMeetingsByUriWithoutDomainV2(uri);
            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<List<Meeting>> v2MeetingsGet(OffsetDateTime fromStartTime, OffsetDateTime toStartTime, String shortId, String subject, String organizedBy, String search, String label, String uriWithDomain) {
        logger.debug("Enter GET meetings, v2.");
        try {
            if (shortId != null && !shortId.isEmpty()) {
                return getMeetingByShortId(shortId);
            } else if (label != null && !label.isEmpty()) {
                return getMeetingsByLabel(label);
            } else if (subject != null && !subject.isEmpty()) {
                return getMeetingsBySubject(subject);
            } else if (organizedBy != null && !organizedBy.isEmpty()) {
                return getMeetingsOrganizedBy(organizedBy);
            } else if (search != null && !search.isEmpty()) {
                return genericSearchMeetings(search, fromStartTime, toStartTime);
            } else if (fromStartTime != null && toStartTime != null) {
                return meetingsGet(fromStartTime, toStartTime);
            } else if (uriWithDomain != null && !uriWithDomain.isEmpty()) {
                return getMeetingsFindByUriWithDomainGet(uriWithDomain);
            } else {
                logger.error("No required parameters given.");
                throw new NotValidDataExceptionV2(DetailedError.DetailedErrorCodeEnum._36, "Must set at least one query parameter, when searching for meeting.");
            }
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (NotValidDataExceptionV2 e) {
            throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    private ResponseEntity<List<Meeting>> meetingsGet(OffsetDateTime fromStartTime, OffsetDateTime toStartTime) {
        logger.debug("Get meetings by fromStartTime: {} toStartTime: {}, v2.", fromStartTime.toString(), toStartTime.toString());

        List<MeetingModel> meetings = meetingService.getMeetingsV2(fromStartTime, toStartTime);

        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    private ResponseEntity<List<Meeting>> genericSearchMeetings(String search, OffsetDateTime fromStartTime, OffsetDateTime toStartTime) {
        logger.debug("Get meetings by search: {}, fromStartTime: {}, toStartTime: {}, v2.", search, fromStartTime, toStartTime);

        if((fromStartTime != null && toStartTime == null) || (fromStartTime == null && toStartTime != null)) {
            try {
                throw new NotValidDataException(DetailedError.DetailedErrorCodeEnum._26, "Either both from-start-time and to-start-time must be provided or none of them must be provided.");
            } catch (NotValidDataExceptionV2 e) {
                throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
            }
        }

        var meetings = meetingService.searchMeetingsV2(search, fromStartTime, toStartTime);
        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    private ResponseEntity<List<Meeting>> getMeetingsByLabel(String label) {
        logger.debug("Get meetings by label: {}, v2.", label);
        var meetings = meetingService.getMeetingsByLabelV2(label);
        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    private ResponseEntity<List<Meeting>> getMeetingByShortId(String shortId) {
        logger.debug("Get meetings by shortId: {}, v2.", shortId);
        var meeting = meetingService.getMeetingByShortIdV2(shortId);
        return ResponseEntity.ok(List.of(VideoMeetingMapper.internalToExternal(meeting)));
    }

    private ResponseEntity<List<Meeting>> getMeetingsBySubject(String subject) {
        logger.debug("Get meetings by subject: {}, v2.", subject);
        var meetings = meetingService.getMeetingsBySubjectV2(subject);
        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    private ResponseEntity<List<Meeting>> getMeetingsOrganizedBy(String organizedBy) {
        logger.debug("Get meetings by organized by: {}, v2.", organizedBy);
        var meetings = meetingService.getMeetingsByOrganizedByV2(organizedBy);
        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    private ResponseEntity<List<Meeting>> getMeetingsFindByUriWithDomainGet(String uri) {
        logger.debug("Get meetings by uri with domain: {}, v2.", uri);
        var meetings = meetingService.getMeetingsByUriWithDomainV2(uri);
        return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meetings));
    }

    @Oauth
    @Override
    @PreAuthorize(plannerProvisionerUserAdminUserScope)
    public ResponseEntity<Meeting> v2MeetingsPost(CreateMeeting createMeeting) {
        logger.debug("Enter POST meetings, v2.");

        var performanceLogger = new PerformanceLogger("create meeting");

        try {
            var meeting = RetryOnException.retry(10, SQLTransactionRollbackException.class, () -> meetingService.createMeetingV2(VideoMeetingMapper.externalToInternal(createMeeting)));
            logger.info("After POST meetings, v2.");

            performanceLogger.logTimeSinceCreation();

            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meeting));

        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (NotValidDataExceptionV2 e) {
            throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(plannerProvisionerUserAdminUserScope)
    public ResponseEntity<Void> v2MeetingsUuidDelete(UUID uuid) {
        logger.debug("Enter DELETE meetings with uuid: {}, v2.", uuid);
        try {
            meetingService.deleteMeetingV2(uuid);

            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<Meeting> v2MeetingsUuidGet(UUID uuid) {
        logger.debug("Enter GET meetings with uuid: {}, v2.", uuid);
        try {
            var meeting = meetingService.getMeetingByUuidV2(uuid);
            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meeting));
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(plannerProvisionerUserAdminUserScope)
    public ResponseEntity<Meeting> v2MeetingsUuidPatch(UUID uuid, PatchMeeting patchMeeting) {
        logger.debug("Enter PATCH meetings with uuid: {}, v2.", uuid);
        try {
            var meeting = meetingService.patchMeetingV2(uuid, VideoMeetingMapper.externalToInternal(patchMeeting));
            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meeting));

        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotValidDataExceptionV2 e) {
            throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(plannerProvisionerUserAdminUserScope)
    public ResponseEntity<Meeting> v2MeetingsUuidPut(UUID uuid, UpdateMeeting updateMeeting) {
        logger.debug("Enter PUT meetings with uuid: {}, v2.", uuid);
        try {
            var meeting = meetingService.updateMeetingV2(uuid, VideoMeetingMapper.externalToInternal(updateMeeting));
            return ResponseEntity.ok(VideoMeetingMapper.internalToExternal(meeting));
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (NotValidDataExceptionV2 e) {
            throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }
}
