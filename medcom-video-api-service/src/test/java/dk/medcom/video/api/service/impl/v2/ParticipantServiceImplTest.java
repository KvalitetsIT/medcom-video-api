package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantDao;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.Participant;
import dk.medcom.video.api.dao.entity.ParticipantRole;
import dk.medcom.video.api.dao.entity.ParticipantType;
import dk.medcom.video.api.service.*;
import dk.medcom.video.api.service.exception.PermissionDeniedExceptionV2;
import dk.medcom.video.api.service.exception.ResourceNotFoundExceptionV2;
import dk.medcom.video.api.service.model.CreateParticipantModel;
import dk.medcom.video.api.service.model.UpdateParticipantModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParticipantServiceImplTest {
    private ParticipantDao participantDao;
    private MeetingUserService meetingUserService;
    private MeetingRepository meetingRepository;
    private UserContextService userContextService;
    private OrganisationService organisationService;
    private ParticipantService participantService;


    @BeforeEach
    public void setup() {
        participantDao = Mockito.mock(ParticipantDao.class);
        meetingUserService = Mockito.mock(MeetingUserService.class);
        meetingRepository = Mockito.mock(MeetingRepository.class);
        userContextService = Mockito.mock(UserContextService.class);
        organisationService = Mockito.mock(OrganisationService.class);
        participantService = new ParticipantServiceImpl(participantDao, meetingRepository, userContextService, meetingUserService, organisationService);
    }

    private void setupValidUserContext(Organisation organisation) {
        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.userIsPermittedForOrganisation(Mockito.any())).thenReturn(true);
    }

    private dk.medcom.video.api.dao.entity.Meeting createMeeting(UUID uuid, Organisation organisation) {
        var meeting = new dk.medcom.video.api.dao.entity.Meeting();
        meeting.setUuid(uuid.toString());
        meeting.setOrganisation(organisation);
        return meeting;
    }

    @Test
    public void testGetParticipants() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());
        setupValidUserContext(meeting.getOrganisation());
        var participants = List.of(new Participant(null, null, null, null, null, null, null), new Participant(null, null, null, null, null, null, null));
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantDao.findByMeeting(meeting)).thenReturn(participants);

        var result = participantService.getParticipants(uuid);

        assertEquals(participants.size(), result.size());
    }

    @Test
    public void testGetParticipantsMeetingNotFound() {
        var uuid = UUID.randomUUID();
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(null);

        assertThrows(ResourceNotFoundExceptionV2.class, () ->
                participantService.getParticipants(uuid));
    }

    @Test
    public void testGetParticipantsUserIsProvisioner() {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(true);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.getParticipants(uuid));
    }

    @Test
    public void testGetParticipantsOrganisationMismatch() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(false);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.getUserOrganisation()).thenReturn(new Organisation());
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.getParticipants(uuid));
    }

    @Test
    public void testCreateParticipants() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        var createParticipants = List.of(
                new CreateParticipantModel(ParticipantType.USER, "ext-id", "org", ParticipantRole.GUEST)
        );
        var savedParticipant = new Participant(null, null, null, null, null, null, null);
        setupValidUserContext(organisation);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantDao.save(Mockito.any())).thenReturn(savedParticipant);

        var result = participantService.createParticipants(uuid, createParticipants);
        assertEquals(1, result.size());
        Mockito.verify(meetingRepository).save(meeting);
    }

    @Test
    public void testCreateParticipantsMeetingNotFound() {
        var uuid = UUID.randomUUID();

        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(null);
        assertThrows(ResourceNotFoundExceptionV2.class, () -> participantService.createParticipants(uuid, List.of()));
    }

    @Test
    public void testCreateParticipantsUserIsProvisioner() {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(true);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.createParticipants(uuid, List.of()));
    }

    @Test
    public void testCreateParticipantsOrganisationMismatch() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(false);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.getUserOrganisation()).thenReturn(new Organisation());
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.createParticipants(uuid, List.of()));
    }

    @Test
    void testUpdateParticipant() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        setupValidUserContext(organisation);
        var updateParticipant = new UpdateParticipantModel(ParticipantRole.GUEST);
        var savedParticipant = new Participant(null, meeting.getId(), meeting.getUuid(), null, null, null, null);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantDao.findById(Mockito.any())).thenReturn(Optional.of(savedParticipant));
        Mockito.when(participantDao.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        var result = participantService.updateParticipant(uuid, 1L, updateParticipant);
        assertEquals(updateParticipant.role(), result.role());
    }

    @Test
    void testUpdateParticipantMeetingNotFound() {
        var uuid = UUID.randomUUID();
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(null);

        assertThrows(ResourceNotFoundExceptionV2.class, () -> participantService.updateParticipant(uuid, 1L, Mockito.any()));
    }

    @Test
    void testUpdateParticipantUserIsProvisioner() {

        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(true);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.updateParticipant(uuid, 1L, Mockito.any()));
    }

    @Test
    void testUpdateParticipantOrganisationMisMatch() throws PermissionDeniedExceptionV2 {

        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(false);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.getUserOrganisation()).thenReturn(new Organisation());
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.updateParticipant(uuid, 1L, Mockito.any()));
    }

    @Test
    void testDeleteParticipant() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        setupValidUserContext(organisation);
        var participantToDelete = new Participant(null, meeting.getId(), meeting.getUuid(), null, null, null, null);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantDao.findById(Mockito.any())).thenReturn(Optional.of(participantToDelete));
        Mockito.when(participantDao.save(Mockito.any())).thenReturn(participantToDelete);
        participantService.deleteParticipant(uuid, participantToDelete.id());

        Mockito.verify(participantDao).delete(participantToDelete);
    }

    @Test
    void testDeleteParticipantMeetingNotFound() {
        var uuid = UUID.randomUUID();
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(null);
        assertThrows(ResourceNotFoundExceptionV2.class, () -> participantService.deleteParticipant(uuid, 1L));
    }

    @Test
    void testDeleteParticipantUserIsProvisioner() {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(true);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.deleteParticipant(uuid, 1L));
    }

    @Test
    void testDeleteParticipantOrganisationMismatch() throws PermissionDeniedExceptionV2 {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());

        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(false);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.getUserOrganisation()).thenReturn(new Organisation());
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);

        assertThrows(PermissionDeniedExceptionV2.class, () ->
                participantService.deleteParticipant(uuid, 1L));
    }

}
