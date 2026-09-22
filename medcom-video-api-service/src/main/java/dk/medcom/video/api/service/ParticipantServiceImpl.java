package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantType;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.hashing.CprHasher;
import dk.medcom.video.api.service.domain.audit.ParticipantSearch;
import dk.medcom.video.api.service.model.CreateParticipantModel;
import dk.medcom.video.api.service.model.ParticipantModel;
import dk.medcom.video.api.service.model.UpdateParticipantModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class ParticipantServiceImpl implements ParticipantService {
    private static final UUID REDACTED_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private static final String REDACTED_VALUE = "*****";

    private final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final ParticipantDao participantDao;
    private final MeetingUserService meetingUserService;
    private final MeetingUserRepository meetingUserRepository;
    private final MeetingRepository meetingRepository;
    private final OrganisationService organisationService;
    private final UserContextService userContextService;
    private final AuditService auditService;
    private final CprHasher cprHasher;

    public ParticipantServiceImpl(ParticipantDao participantDao, MeetingRepository meetingRepository, MeetingUserService meetingUserService, MeetingUserRepository meetingUserRepository, OrganisationService organisationService, UserContextService userContextService, AuditService auditService, CprHasher cprHasher) {
        this.participantDao = participantDao;
        this.meetingRepository = meetingRepository;
        this.meetingUserService = meetingUserService;
        this.meetingUserRepository = meetingUserRepository;
        this.organisationService = organisationService;
        this.userContextService = userContextService;
        this.auditService = auditService;
        this.cprHasher = cprHasher;
    }

    @Override
    public List<ParticipantModel> getParticipants(UUID meetingUuid) {
        logger.debug("Get participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        var participants = participantDao.findByMeeting(meeting).stream().map(this::toModel).toList();
        auditGetParticipants(meetingUuid, participants);

        return userContextService.getUserContext().hasRole(UserRole.CITIZEN_LOOKUP)
                ? participants
                : participants.stream().map(this::redactIfCitizen).toList();
    }

    private void auditGetParticipants(UUID meetingUuid, List<ParticipantModel> participants) {
        var userContext = userContextService.getUserContext();
        var search = new ParticipantSearch();
        search.setMeetingUuid(meetingUuid.toString());
        search.setOrganisation(userContext.getUserOrganisation());
        search.setPerformedBy(userContext.getUserEmail());
        search.setResultCount(participants.size());
        search.setResultIdentifiers(participants.stream().map(p -> String.valueOf(p.uuid())).toList());

        auditService.auditParticipantSearch(search, "list");
    }



    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<ParticipantModel> createParticipants(UUID meetingUuid, List<CreateParticipantModel> createParticipantModel) {
        logger.debug("Create participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);

        var containsCitizen = createParticipantModel.stream().anyMatch(p -> p.type() == ParticipantType.CITIZEN);
        if (containsCitizen && !userContextService.getUserContext().hasRole(UserRole.CITIZEN_LOOKUP)) {
            throw new PermissionDeniedExceptionV2();
        }

        var currentUser = meetingUserService.getOrCreateCurrentMeetingUser();

        var participants = createParticipantModel.stream().map(p -> {

            String organisation = p.organisation();
            var participantId = p.participantId();

            if (p.type() == ParticipantType.CITIZEN) {
                participantId = cprHasher.hash(p.participantId());
            }

            if (p.type() == ParticipantType.ORGANISATION) {
                var org = organisationService.getParticipantOrganisation(p.participantId());
                if (org == null) {
                    logger.info("Organisation participant references unknown organisation: {}", p.participantId());
                    throw new ResourceNotFoundExceptionV2("organisation", "participantId");
                }
                organisation = org.getOrganisationId();
            }

            var participant = new Participant(
                    null,
                    UUID.randomUUID(),
                    meeting.getId(),
                    UUID.fromString(meeting.getUuid()),
                    p.type(),
                    participantId,
                    organisation,
                    p.role(),
                    null,
                    currentUser.getId(),
                    null,
                    currentUser.getId());
            return toModel(participantDao.save(participant));
        }).toList();

        updateMeeting(meeting);
        return participants;
    }

    @Override
    public void deleteParticipant(UUID meetingUuid, UUID participantId) {
        logger.debug("Delete participant {} for meeting {}.", participantId, meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        var participant = participantDao.findByUuId(participantId)
                .orElseThrow(() -> new ResourceNotFoundExceptionV2("participant", "id"));
        if (!participant.meetingUuid().equals(meetingUuid)) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        participantDao.delete(participant);
        updateMeeting(meeting);
    }

    @Override
    public ParticipantModel updateParticipant(UUID meetingUuid, UUID participantId, UpdateParticipantModel updateParticipant) {
        logger.debug("Update participant {} for meeting {}.", meetingUuid, participantId);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        var participant = participantDao.findByUuId(participantId).orElseThrow(() -> new ResourceNotFoundExceptionV2("participant", "id"));
        if (!participant.meetingUuid().equals(meetingUuid)) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        var currentUser = meetingUserService.getOrCreateCurrentMeetingUser();
        var updated = new Participant(
                participant.id(),
                participant.uuid(),
                participant.meetingId(),
                participant.meetingUuid(),
                participant.type(),
                participant.participantId(),
                participant.organisationId(),
                updateParticipant.role(),
                participant.createdAt(),
                participant.createdBy(),
                participant.updatedAt(),
                currentUser.getId());
        var saved = participantDao.save(updated);

        updateMeeting(meeting);

        return toModel(saved);
    }

    private ParticipantModel toModel(Participant participant) {
        MeetingUser createdByUser = participant.createdBy() != null
                ? meetingUserRepository.findById(participant.createdBy()).orElse(null)
                : null;
        MeetingUser updatedByUser = participant.updatedBy() != null
                ? meetingUserRepository.findById(participant.updatedBy()).orElse(null)
                : null;
        return ParticipantModel.from(participant, createdByUser, updatedByUser);
    }

    private void validateUser(Meeting meeting) {
        if (meeting == null) {
            throw new ResourceNotFoundExceptionV2("meeting", "uuid");
        }
        if (!organisationService.userIsPermittedForOrganisation(meeting.getOrganisation().getOrganisationId())) {
            throw new PermissionDeniedExceptionV2();
        }
    }


    private void updateMeeting(Meeting meeting) {
        meeting.setUpdatedTime(new GregorianCalendar().getTime());
        try {
            meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
        } catch (PermissionDeniedExceptionV2 e) {
            throw new PermissionDeniedExceptionV2();
        }
        meetingRepository.save(meeting);
    }

    private ParticipantModel redactIfCitizen(ParticipantModel participant) {
        if (participant.type() != ParticipantType.CITIZEN) {
            return participant;
        }
        return new ParticipantModel(
                participant.id(),
                REDACTED_UUID,
                participant.type(),
                REDACTED_VALUE,
                REDACTED_VALUE,
                participant.role(),
                participant.createdTime(),
                participant.createdBy(),
                participant.updatedTime(),
                participant.updatedBy());
    }
}