package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Participant;
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
    private final MeetingRepository meetingRepository;
    private final OrganisationService organisationService;

    public ParticipantServiceImpl(ParticipantDao participantDao, MeetingRepository meetingRepository, UserContextService userContextService, MeetingUserService meetingUserService, OrganisationService organisationService) {
        this.participantDao = participantDao;
        this.meetingRepository = meetingRepository;
        this.meetingUserService = meetingUserService;
        this.organisationService = organisationService;
    }

    @Override
    public List<ParticipantModel> getParticipants(UUID meetingUuid) {
        logger.debug("Get participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        return participantDao.findByMeeting(meeting).stream().map(ParticipantModel::from).toList();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<ParticipantModel> createParticipants(UUID meetingUuid, List<CreateParticipantModel> createParticipantModel) {
        logger.debug("Create participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        validateUser(meeting);
        var participants = createParticipantModel.stream().map(p -> {
            var participant = new Participant(
                    null,
                    UUID.randomUUID(),
                    meeting.getId(),
                    meeting.getUuid(),
                    p.type(),
                    p.externalId(),
                    p.organisation(),
                    p.role());
            return ParticipantModel.from(participantDao.save(participant));
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
        if (!participant.meetingUuid().equals(meetingUuid.toString())) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        participantDao.delete(participant);
        updateMeeting(meeting);
    }

    @Override
    public ParticipantModel updateParticipant(UUID uuid, UUID id, UpdateParticipantModel updateParticipant) {
        var meeting = meetingRepository.findOneByUuid(uuid.toString());
        validateUser(meeting);
        var participant = participantDao.findByUuId(id).orElseThrow(() -> new ResourceNotFoundExceptionV2("participant", "id"));
        if (!participant.meetingUuid().equals(uuid.toString())) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        var updated = new Participant(
                participant.id(),
                participant.uuid(),
                participant.meetingId(),
                participant.meetingUuid(),
                participant.type(),
                participant.externalId(),
                participant.organisation(),
                updateParticipant.role());
        var saved = participantDao.save(updated);

        updateMeeting(meeting);

        return ParticipantModel.from(saved);
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