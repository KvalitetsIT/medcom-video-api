package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.service.exception.*;
import dk.medcom.video.api.service.mapper.v2.EnumMapper;
import dk.medcom.video.api.service.mapper.v2.SchedulingInfoMapper;
import dk.medcom.video.api.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SchedulingInfoServiceV2Impl implements SchedulingInfoServiceV2 {
    private final Logger logger = LoggerFactory.getLogger(SchedulingInfoServiceV2Impl.class);

    private final SchedulingInfoService schedulingInfoService;
    private final String shortLinkBaseUrl;

    public SchedulingInfoServiceV2Impl(SchedulingInfoService schedulingInfoService, String shortLinkBaseUrl) {
        this.schedulingInfoService = schedulingInfoService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
    }

    @Override
    public List<SchedulingInfoModel> getSchedulingInfoV2(OffsetDateTime fromStartTime, OffsetDateTime toEndTime, ProvisionStatusModel provisionStatus) {
        logger.debug("Get scheduling info, v2.");
        return schedulingInfoService.getSchedulingInfo(Date.from(fromStartTime.toInstant()), Date.from(toEndTime.toInstant()), ProvisionStatus.valueOf(provisionStatus.toString()))
                .stream().map(schedulingInfo -> SchedulingInfoModel.from(schedulingInfo, shortLinkBaseUrl)).toList();
    }

    @Override
    public List<SchedulingInfoModel> getSchedulingInfoAwaitsProvisionV2() {
        logger.debug("Get scheduling info awaits provision, v2.");
        return schedulingInfoService.getSchedulingInfoAwaitsProvision()
                .stream().map(schedulingInfo -> SchedulingInfoModel.from(schedulingInfo, shortLinkBaseUrl)).toList();
    }

    @Override
    public List<SchedulingInfoModel> getSchedulingInfoAwaitsDeProvisionV2() {
        logger.debug("Get scheduling info awaits deprovision, v2.");
        return schedulingInfoService.getSchedulingInfoAwaitsDeProvision()
                .stream().map(schedulingInfo -> SchedulingInfoModel.from(schedulingInfo, shortLinkBaseUrl)).toList();
    }

    @Override
    public SchedulingInfoModel getSchedulingInfoByUuidV2(UUID uuid) {
        logger.debug("Get scheduling info by uuid, v2.");
        try {
            return SchedulingInfoModel.from(schedulingInfoService.getSchedulingInfoByUuid(uuid.toString()), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }


    @Override
    public SchedulingInfoModel createSchedulingInfoV2(CreateSchedulingInfoModel createSchedulingInfo) {
        logger.debug("Create scheduling info, v2.");
        try {
            return SchedulingInfoModel.from(schedulingInfoService.createSchedulingInfo(SchedulingInfoMapper.modelToDto(createSchedulingInfo)), shortLinkBaseUrl);
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        } catch (NotValidDataException e) {
            throw new NotValidDataExceptionV2(ExceptionMapper.fromNotValidData(e.getErrorCode()), e.getErrorText());
        } catch (NotAcceptableException e) {
            throw new NotAcceptableExceptionV2(ExceptionMapper.fromNotAcceptable(e.getErrorCode()), e.getErrorText());
        }
    }

    @Override
    public SchedulingInfoModel updateSchedulingInfoV2(UUID uuid, UpdateSchedulingInfoModel updateSchedulingInfo) {
        logger.debug("Update scheduling info, v2.");
        try {
            return SchedulingInfoModel.from(schedulingInfoService.updateSchedulingInfo(uuid.toString(), SchedulingInfoMapper.modelToDto(updateSchedulingInfo)), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public SchedulingInfoModel reserveSchedulingInfoV2(VmrTypeModel vmrType, ViewTypeModel hostView, ViewTypeModel guestView, VmrQualityModel vmrQuality, Boolean enableOverlayText, Boolean guestsCanPresent, Boolean forcePresenterIntoMain, Boolean forceEncryption, Boolean muteAllGuests) {
        logger.debug("Reserve scheduling info, v2.");
        try {
            return SchedulingInfoModel.from(schedulingInfoService.reserveSchedulingInfo(EnumMapper.modelToEntity(vmrType), EnumMapper.modelToEntity(hostView),
                    EnumMapper.modelToEntity(guestView), EnumMapper.modelToEntity(vmrQuality), enableOverlayText, guestsCanPresent,
                    forcePresenterIntoMain, forceEncryption, muteAllGuests), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }

    @Override
    public SchedulingInfoModel getSchedulingInfoByReservationV2(UUID schedulingInfoReservationId) {
        logger.debug("Get scheduling info by reservation id, v2.");
        try {
            return SchedulingInfoModel.from(schedulingInfoService.getSchedulingInfoByReservation(schedulingInfoReservationId), shortLinkBaseUrl);
        } catch (RessourceNotFoundException e) {
            throw new ResourceNotFoundExceptionV2(e.getRessource(), e.getField());
        }
    }
}
