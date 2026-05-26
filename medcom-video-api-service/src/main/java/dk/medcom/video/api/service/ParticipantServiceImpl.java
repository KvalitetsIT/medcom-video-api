package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantRepository;
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
    private final ParticipantRepository participantRepository;
    private final MeetingUserService meetingUserService;
    private final MeetingRepository meetingRepository;
    private final UserContextService userContextService;
    private final OrganisationService organisationService;

    public ParticipantServiceImpl(ParticipantRepository participantRepository, MeetingRepository meetingRepository, UserContextService userContextService, MeetingUserService meetingUserService, OrganisationService organisationService) {
        this.participantRepository = participantRepository;
        this.meetingRepository = meetingRepository;
        this.userContextService = userContextService;
        this.meetingUserService = meetingUserService;
        this.organisationService = organisationService;
    }

    @Override
    public List<ParticipantModel> getParticipants(UUID meetingUuid) {
        logger.debug("Get participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        ValidateUser(meeting);
        return participantRepository.findByMeeting(meeting).stream().map(ParticipantModel::from).toList();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<ParticipantModel> createParticipants(UUID meetingUuid, List<CreateParticipantModel> createParticipantModel) {
        logger.debug("Create participants for meeting {}.", meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        ValidateUser(meeting);
        var participants = createParticipantModel.stream().map(p -> {
            var participant = new Participant();
            participant.setMeeting(meeting);
            participant.setType(p.type());
            participant.setExternalId(p.externalId());
            participant.setOrganisation(p.organisation());
            participant.setRole(p.role());
            return ParticipantModel.from(participantRepository.save(participant));
        }).toList();
        UpdateMeeting(meeting);
        return participants;
    }

    @Override
    public void deleteParticipant(UUID meetingUuid, Long participantId) {
        logger.debug("Delete participant {} for meeting {}.", participantId, meetingUuid);
        var meeting = meetingRepository.findOneByUuid(meetingUuid.toString());
        ValidateUser(meeting);
        var participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundExceptionV2("participant", "id"));
        if (!participant.getMeeting().getUuid().equals(meetingUuid.toString())) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        participantRepository.delete(participant);
        UpdateMeeting(meeting);
    }

    @Override
    public ParticipantModel updateParticipant(UUID uuid, Long id, UpdateParticipantModel updateParticipant) {
        var meeting = meetingRepository.findOneByUuid(uuid.toString());
        ValidateUser(meeting);
        var participant = participantRepository.findById(id).orElseThrow(() -> new ResourceNotFoundExceptionV2("participant", "id"));
        if (!participant.getMeeting().getUuid().equals(uuid.toString())) {
            throw new ResourceNotFoundExceptionV2("participant", "id");
        }
        participant.setRole(updateParticipant.role());
        var saved = participantRepository.save(participant);

        UpdateMeeting(meeting);

        return ParticipantModel.from(saved);
    }

    private void ValidateUser(Meeting meeting) {
        if (meeting == null) {
            throw new ResourceNotFoundExceptionV2("meeting", "uuid");
        }
        if (userContextService.getUserContext().hasOnlyRole(UserRole.PROVISIONER)) {
            throw new PermissionDeniedExceptionV2();
        }
        try {
            if (!meeting.getOrganisation().equals(organisationService.getUserOrganisation())) {
                throw new PermissionDeniedExceptionV2();
            }
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
    }

    private void UpdateMeeting(Meeting meeting) {
        meeting.setUpdatedTime(new GregorianCalendar().getTime());
        try {
            meeting.setUpdatedByUser(meetingUserService.getOrCreateCurrentMeetingUser());
        } catch (PermissionDeniedException e) {
            throw new PermissionDeniedExceptionV2();
        }
        meetingRepository.save(meeting);
    }
}