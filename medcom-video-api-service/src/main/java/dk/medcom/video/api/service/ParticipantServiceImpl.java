package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantType;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
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
    private final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final ParticipantDao participantDao;
    private final MeetingUserService meetingUserService;
    private final MeetingUserRepository meetingUserRepository;
    private final MeetingRepository meetingRepository;
    private final OrganisationService organisationService;

    public ParticipantServiceImpl(ParticipantDao participantDao, MeetingRepository meetingRepository, MeetingUserService meetingUserService, MeetingUserRepository meetingUserRepository, OrganisationService organisationService) {
        this.participantDao = participantDao;
        this.meetingRepository = meetingRepository;
        this.meetingUserService = meetingUserService;
        this.meetingUserRepository = meetingUserRepository;
        this.organisationService = organisationService;
    }

    @Override
    public List<ParticipantModel> getParticipants(UUID meetingUuid) {
        logger.debug("Get participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        return participantDao.findByMeeting(meeting).stream().map(this::toModel).toList();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<ParticipantModel> createParticipants(UUID meetingUuid, List<CreateParticipantModel> createParticipantModel) {
        logger.debug("Create participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        var currentUser = meetingUserService.getOrCreateCurrentMeetingUser();

        var participants = createParticipantModel.stream().map(p -> {
            String organisation = p.organisation();

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
                    p.participantId(),
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
}