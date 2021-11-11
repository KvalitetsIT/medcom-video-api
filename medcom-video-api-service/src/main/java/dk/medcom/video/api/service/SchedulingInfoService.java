package dk.medcom.video.api.service;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SchedulingInfoService {
    List<SchedulingInfo> getSchedulingInfo(Date fromStartTime, Date toEndTime, ProvisionStatus provisionStatus);

    List<SchedulingInfo> getSchedulingInfoAwaitsProvision();

    List<SchedulingInfo> getSchedulingInfoAwaitsDeProvision();

    SchedulingInfo getSchedulingInfoByUuid(String uuid) throws RessourceNotFoundException;

    @Transactional(rollbackFor = Throwable.class)
    SchedulingInfo createSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto) throws NotAcceptableException, PermissionDeniedException, NotValidDataException;

    @Transactional(rollbackFor = Throwable.class)
    SchedulingInfo updateSchedulingInfo(String uuid, UpdateSchedulingInfoDto updateSchedulingInfoDto) throws RessourceNotFoundException, PermissionDeniedException;

    //used by meetingService to update VMRStarttime and portalLink because it depends on the meetings starttime
    @Transactional(rollbackFor = Throwable.class)
    SchedulingInfo updateSchedulingInfo(String uuid, Date startTime) throws RessourceNotFoundException, PermissionDeniedException;

    @Transactional(rollbackFor = Throwable.class)
    void deleteSchedulingInfo(String uuid) throws RessourceNotFoundException;

    @Transactional(rollbackFor = Throwable.class)
    SchedulingInfo createSchedulingInfo(CreateSchedulingInfoDto createSchedulingInfoDto) throws PermissionDeniedException, NotValidDataException, NotAcceptableException;

    SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting, SchedulingInfo schedulingInfo, boolean fromOverflow);

    SchedulingInfo attachMeetingToSchedulingInfo(Meeting meeting, CreateMeetingDto createMeetingDto);

    SchedulingInfo reserveSchedulingInfo(VmrType vmrType,
                                         ViewType hostView,
                                         ViewType guestView,
                                         VmrQuality vmrQuality,
                                         Boolean enableOverlayText,
                                         Boolean guestsCanPresent,
                                         Boolean forcePresenterIntoMain,
                                         Boolean forceEncryption,
                                         Boolean muteAllGuests) throws RessourceNotFoundException;

    SchedulingInfo getSchedulingInfoByReservation(UUID schedulingInfoReservationId) throws RessourceNotFoundException;
}
