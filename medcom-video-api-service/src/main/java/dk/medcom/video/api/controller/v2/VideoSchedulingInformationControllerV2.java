package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.PerformanceLogger;
import dk.medcom.video.api.controller.v2.exception.*;
import dk.medcom.video.api.controller.v2.mapper.EnumMapper;
import dk.medcom.video.api.controller.v2.mapper.VideoSchedulingMapper;
import dk.medcom.video.api.interceptor.Oauth;
import dk.medcom.video.api.service.SchedulingInfoServiceV2;
import dk.medcom.video.api.service.exception.NotAcceptableExceptionV2;
import dk.medcom.video.api.service.exception.NotValidDataExceptionV2;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import org.openapitools.api.VideoSchedulingInformationV2Api;
import org.openapitools.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class VideoSchedulingInformationControllerV2 implements VideoSchedulingInformationV2Api {
    private final static Logger logger = LoggerFactory.getLogger(VideoSchedulingInformationControllerV2.class);

    private final String anyScope = "hasAnyAuthority('SCOPE_meeting-user','SCOPE_meeting-admin','SCOPE_meeting-provisioner','SCOPE_meeting-provisioner-user','SCOPE_meeting-planner','SCOPE_undefined')";
    private final String adminScope = "hasAuthority('SCOPE_meeting-admin')";
    private final String provisionerUserScope = "hasAuthority('SCOPE_meeting-provisioner-user')";

    private final SchedulingInfoServiceV2 schedulingInfoService;

    public VideoSchedulingInformationControllerV2(SchedulingInfoServiceV2 schedulingInfoService) {
        this.schedulingInfoService = schedulingInfoService;
    }

    @Oauth
    @Override
    @PreAuthorize(provisionerUserScope)
    public ResponseEntity<List<SchedulingInfo>> v2SchedulingInfoDeprovisionGet() {
        logger.debug("Enter GET scheduling info deprovision, v2.");
        try {
            var schedulingInfos = schedulingInfoService.getSchedulingInfoAwaitsDeProvisionV2();

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfos));
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }

    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<List<SchedulingInfo>> v2SchedulingInfoGet(OffsetDateTime fromStartTime, OffsetDateTime toEndTime, ProvisionStatus provisionStatus) {
        logger.debug("Enter GET scheduling info with fromStartTime: {} toEndTime: {} provision status: {}, v2.", fromStartTime, toEndTime, provisionStatus);
        try {
            var schedulingInfos = schedulingInfoService.getSchedulingInfoV2(fromStartTime, toEndTime, EnumMapper.externalToInternal(provisionStatus));

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfos));
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(provisionerUserScope)
    public ResponseEntity<SchedulingInfo> v2SchedulingInfoPost(CreateSchedulingInfo createSchedulingInfo) {
        logger.debug("Enter POST scheduling info, v2.");
        try {
            var schedulingInfo = schedulingInfoService.createSchedulingInfoV2(VideoSchedulingMapper.externalToInternal(createSchedulingInfo));

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfo));
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (NotValidDataExceptionV2 e) {
            throw new NotValidDataException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (NotAcceptableExceptionV2 e) {
            throw new NotAcceptableException(e.getDetailedErrorCode(), e.getDetailedError());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }

    }

    @Oauth
    @Override
    @PreAuthorize(provisionerUserScope)
    public ResponseEntity<List<SchedulingInfo>> v2SchedulingInfoProvisionGet() {
        logger.debug("Enter GET scheduling info provision, v2.");
        try {
            var schedulingInfos = schedulingInfoService.getSchedulingInfoAwaitsProvisionV2();
            logger.debug("getSchedulingInfoAwaitsProvision returned ID's: {}.", schedulingInfos.stream().map(x -> x.uuid().toString()).collect(Collectors.joining(",")));

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfos));
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminScope)
    public ResponseEntity<SchedulingInfo> v2SchedulingInfoReserveGet(VmrType vmrType, ViewType hostView, ViewType guestView, VmrQuality vmrQuality, Boolean enableOverlayText, Boolean guestsCanPresent, Boolean forcePresenterIntoMain, Boolean forceEncryption, Boolean muteAllGuests) {
        logger.debug("Enter GET scheduling info reserve, v2.");
        try {
            var schedulingInfo = schedulingInfoService.reserveSchedulingInfoV2(
                    EnumMapper.externalToInternal(vmrType), EnumMapper.externalToInternal(hostView),
                    EnumMapper.externalToInternal(guestView), EnumMapper.externalToInternal(vmrQuality),
                    enableOverlayText, guestsCanPresent, forcePresenterIntoMain, forceEncryption, muteAllGuests);

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfo));
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(adminScope)
    public ResponseEntity<SchedulingInfo> v2SchedulingInfoReserveUuidGet(UUID uuid) {
        logger.debug("Enter GET scheduling info reserve with reservation id: {}, v2.", uuid);
        try {
            var schedulingInfo = schedulingInfoService.getSchedulingInfoByReservationV2(uuid);

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfo));
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
    public ResponseEntity<SchedulingInfo> v2SchedulingInfoUuidGet(UUID uuid) {
        logger.debug("Enter GET scheduling info with uuid: {}, v2.", uuid);
        var performanceLogger = new PerformanceLogger("get scheduling info");
        try {
            var schedulingInfo = schedulingInfoService.getSchedulingInfoByUuidV2(uuid);

            performanceLogger.logTimeSinceCreation();

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfo));
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }

    @Oauth
    @Override
    @PreAuthorize(provisionerUserScope)
    public ResponseEntity<SchedulingInfo> v2SchedulingInfoUuidPut(UUID uuid, UpdateSchedulingInfo updateSchedulingInfo) {
        logger.debug("Enter PUT scheduling info with uuid: {}, vmr id: {}, status: {}, status description: {}, v2.", uuid, updateSchedulingInfo.getProvisionVmrId(), updateSchedulingInfo.getProvisionStatus(), updateSchedulingInfo.getProvisionStatusDescription());
        try {
            var schedulingInfo = schedulingInfoService.updateSchedulingInfoV2(uuid, VideoSchedulingMapper.externalToInternal(updateSchedulingInfo));

            return ResponseEntity.ok(VideoSchedulingMapper.internalToExternal(schedulingInfo));
        } catch (ResourceNotFoundExceptionV2 e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedException(e.getMessage());
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException("Unexpected exception caught. " + e);
        }
    }
}
