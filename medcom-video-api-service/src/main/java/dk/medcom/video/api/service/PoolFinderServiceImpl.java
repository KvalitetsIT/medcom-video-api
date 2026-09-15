package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class PoolFinderServiceImpl implements PoolFinderService {
    private static final Logger logger = LoggerFactory.getLogger(PoolFinderServiceImpl.class);

    private final SchedulingInfoRepository schedulingInfoRepository;
    private final int meetingMinimumAgeSec;

    public PoolFinderServiceImpl(SchedulingInfoRepository schedulingInfoRepository, int meetingMinimumAgeSec) {
        this.schedulingInfoRepository = schedulingInfoRepository;
        this.meetingMinimumAgeSec = meetingMinimumAgeSec;
    }

    @Override
    public Optional<SchedulingInfo> findPoolSubject(Organisation organisation, CreateMeetingDto createMeetingDto) {
        Instant provisionTimestampOlderThenInstant = Instant.now().minus(meetingMinimumAgeSec, ChronoUnit.SECONDS);
        Date provisionTimestampOlderThen = Date.from(provisionTimestampOlderThenInstant);

        String vmrType = null, hostView = null, guestView = null, vmrQuality = null, callType = null;
        Boolean enableOverlay = null, guestsPresent = null, forcePresenter = null, forceEncryption = null, muteGuests = null;

        if (createMeetingDto != null) {
            vmrType = createMeetingDto.getVmrType() != null ? createMeetingDto.getVmrType().name() : null;
            hostView = createMeetingDto.getHostView() != null ? createMeetingDto.getHostView().name() : null;
            guestView = createMeetingDto.getGuestView() != null ? createMeetingDto.getGuestView().name() : null;
            vmrQuality = createMeetingDto.getVmrQuality() != null ? createMeetingDto.getVmrQuality().name() : null;

            enableOverlay = createMeetingDto.getEnableOverlayText();
            guestsPresent = createMeetingDto.getGuestsCanPresent();
            forcePresenter = createMeetingDto.getForcePresenterIntoMain();
            forceEncryption = createMeetingDto.getForceEncryption();
            muteGuests = createMeetingDto.getMuteAllGuests();

            callType = createMeetingDto.getCallType();
        }

        logger.debug("findByMeetingIsNullAndOrganisationAndProvisionStatus - " +
                        "Org: '{}' Time: '{}' VMR Type: '{}' HostView: '{}' GuestView: '{}' " +
                        "VmrQuality: '{}' EnableOverlayText: '{}' GuestsCanPresent: '{}' " +
                        "ForcePresenterIntoMain: '{}' ForceEncryption: '{}' MuteAllGuests: '{}' CallType '{}'",
                organisation != null ? organisation.getId() : null,
                provisionTimestampOlderThen, vmrType, hostView, guestView,
                vmrQuality, enableOverlay, guestsPresent, forcePresenter,
                forceEncryption, muteGuests, callType);

        return schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                        organisation != null ? organisation.getId() : null,
                        ProvisionStatus.PROVISIONED_OK.name(),
                        provisionTimestampOlderThen,
                        vmrType, hostView, guestView, vmrQuality, callType,
                        enableOverlay, guestsPresent, forcePresenter, forceEncryption, muteGuests
                )
                .stream()
                .findFirst();
    }

}
