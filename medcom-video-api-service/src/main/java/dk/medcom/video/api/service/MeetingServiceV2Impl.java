package dk.medcom.video.api.service;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.filter.MeetingParticipationFilter;
import dk.medcom.video.api.service.exception.*;
import dk.medcom.video.api.service.mapper.v2.MeetingMapper;
import dk.medcom.video.api.service.model.*;
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
    private final ParticipantDao participantDao;
    private final MeetingRepository meetingRepository;
    private final SchedulingInfoRepository schedulingInfoRepository;

    public MeetingServiceV2Impl(MeetingService meetingService,
                                String shortLinkBaseUrl,
                                ParticipantDao participantDao,
                                MeetingRepository meetingRepository,
                                SchedulingInfoRepository schedulingInfoRepository) {
        this.meetingService = meetingService;
        this.shortLinkBaseUrl = shortLinkBaseUrl;
        this.participantDao = participantDao;
        this.meetingRepository = meetingRepository;
        this.schedulingInfoRepository = schedulingInfoRepository;
    }

    private MeetingModel toModel(dk.medcom.video.api.dao.entity.Meeting meeting) {
        return MeetingModel.from(meeting, shortLinkBaseUrl, meeting.getParticipantCount());
    }

    @Override
    public List<MeetingModel> getMeetingsV2(OffsetDateTime fromStartTime, OffsetDateTime toStartTime) {
        logger.debug("Get meetings by start time, v2.");
        try {
            return meetingService.getMeetings(Date.from(fromStartTime.toInstant()), Date.from(toStartTime.toInstant()))
                    .stream().map(meeting -> MeetingModel.from(meeting, shortLinkBaseUrl, meeting.getParticipantCount())).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingByShortIdV2(String shortId) {
        logger.debug("Get meeting by short id, v2.");
        try {
            return toModel(meetingService.getMeetingByShortId(shortId));
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
                    .stream().map(this::toModel).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> getMeetingsByOrganizedByV2(String organizedBy) {
        logger.debug("Get meetings by organized by, v2.");
        try {
            return meetingService.getMeetingsByOrganizedBy(organizedBy)
                    .stream().map(this::toModel).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public List<MeetingModel> getMeetingsByUriWithDomainV2(String uriWithDomain) {
        logger.debug("Get meetings by uri with domain, v2.");
        try {
            return meetingService.getMeetingsByUriWithDomain(uriWithDomain)
                    .stream().map(this::toModel).toList();
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
                    .stream().map(this::toModel).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingsByUriWithDomainSingleV2(String uriWithDomain) {
        logger.debug("Get meeting by uri without domain, v2.");
        try {
            return toModel(meetingService.getMeetingsByUriWithDomainSingle(uriWithDomain));
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
            return toModel(meetingService.getMeetingsByUriWithoutDomain(uriWithoutDomain));
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
                    .stream().map(this::toModel).toList();
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    @Override
    public MeetingModel getMeetingByUuidV2(UUID uuid) {
        logger.debug("Get meeting by uuid, v2.");
        try {
            return toModel(meetingService.getMeetingByUuid(uuid.toString()));
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
            var meeting = meetingService.createMeeting(MeetingMapper.modelToDto(createMeeting));
            if (createMeeting.participants() != null) {
                createMeeting.participants().forEach(p -> {
                    var participant = new Participant(
                            null,
                            UUID.randomUUID(),
                            meeting.getId(),
                            UUID.fromString(meeting.getUuid()),
                            p.type(),
                            p.participantId(),
                            p.organisation(),
                            p.role(),
                            null,
                            null,
                            null,
                            null);

                    participantDao.save(participant);
                });
            }
            return toModel(meeting);
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
            return toModel(meetingService.updateMeeting(uuid.toString(), MeetingMapper.modelToDto(updateMeeting)));
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
            return toModel(meetingService.patchMeeting(uuid, MeetingMapper.modelToDto(patchMeeting)));
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

    @Override
    public List<MeetingParticipationModel> getMeetingParticipations(String participantId,
                                                                    OffsetDateTime fromStartTime,
                                                                    OffsetDateTime toStartTime) {
        logger.debug("Get meeting participations for participant, v2.");
        var participants = participantDao.findByParticipantId(participantId);
        if (participants.isEmpty()) {
            return List.of();
        }

        var meetingIds = participants.stream().map(Participant::meetingId).toList();
        var meetingsById = new java.util.HashMap<Long, Meeting>();
        meetingRepository.findAllById(meetingIds).forEach(m -> meetingsById.put(m.getId(), m));

        var participantAndMeetingMap = new java.util.LinkedHashMap<Participant, Meeting>();
        for (var participant : participants) {
            var meeting = meetingsById.get(participant.meetingId());
            if (meeting == null) {
                throw new ResourceNotFoundExceptionV2("meeting", "id");
            }
            if (MeetingParticipationFilter.matches(meeting, fromStartTime, toStartTime)) {
                participantAndMeetingMap.put(participant, meeting);
            }
        }

        var filteredMeetings = participantAndMeetingMap.values().stream().toList();
        var schedulingInfoByMeetingId = new java.util.LinkedHashMap<Long, SchedulingInfo>();
        schedulingInfoRepository.findByMeetingIn(filteredMeetings)
                .forEach(si -> schedulingInfoByMeetingId.put(si.getMeeting().getId(), si));

        var result = new java.util.ArrayList<MeetingParticipationModel>();
        for (var entry : participantAndMeetingMap.entrySet()) {
            var participant = entry.getKey();
            var meeting = entry.getValue();
            var schedulingInfo = schedulingInfoByMeetingId.get(meeting.getId());

            Long pin = schedulingInfo == null ? null
                    : participant.role() == ParticipantRole.HOST ? schedulingInfo.getHostPin() : schedulingInfo.getGuestPin();

            result.add(MeetingParticipationModel.from(
                    meeting,
                    schedulingInfo,
                    meeting.getParticipantCount(),
                    participant.role(),
                    pin != null ? pin.intValue() : 0,
                    shortLinkBaseUrl));
        }
        return result;
    }
}