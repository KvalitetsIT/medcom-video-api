package dk.medcom.video.api.service;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

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
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - meetingMinimumAgeSec);

        logger.debug("findByMeetingIsNullAndOrganisationAndProvisionStatus - Org: '{}' Time: '{}' VMR Type: '{}' HostView: '{}' GuestView: '{}' VmrQuality: '{}' EnableOverlayText: '{}' GuestsCanPresent: '{}' ForcePresenterIntoMain: '{}' ForceEncryption: '{}' MuteAllGuests: '{}'",
                organisation.getId(),
                cal.getTime(),
                nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getVmrType()),
                nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getHostView()),
                nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getGuestView()),
                nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getVmrQuality()),
                nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getEnableOverlayText()),
                nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getGuestsCanPresent()),
                nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getForcePresenterIntoMain()),
                nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getForceEncryption()),
                nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getMuteAllGuests()));

        return schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                        organisation.getId(),
                        ProvisionStatus.PROVISIONED_OK.name(),
                        cal.getTime(),
                        nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getVmrType()),
                        nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getHostView()),
                        nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getGuestView()),
                        nullOrFieldValueString(createMeetingDto, () -> createMeetingDto.getVmrQuality()),
                        nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getEnableOverlayText()),
                        nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getGuestsCanPresent()),
                        nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getForcePresenterIntoMain()),
                        nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getForceEncryption()),
                        nullOrFieldValueBoolean(createMeetingDto, () -> createMeetingDto.getMuteAllGuests()))
                .stream()
                .findFirst();
    }

    private String nullOrFieldValueString(Object o , Supplier<Enum<?>> input) {
        if(o == null) {
            return null;
        }

        return input.get() == null ? null : input.get().name();
    }

    private Boolean nullOrFieldValueBoolean(Object o, Supplier<Boolean> input) {
        if(o == null) {
            return null;
        }

        return input.get();
    }

}
