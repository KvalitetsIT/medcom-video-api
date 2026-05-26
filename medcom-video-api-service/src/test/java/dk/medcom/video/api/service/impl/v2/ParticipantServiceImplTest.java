package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.MeetingRepository;
import dk.medcom.video.api.dao.ParticipantRepository;
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
    private ParticipantRepository participantRepository;
    private MeetingUserService meetingUserService;
    private MeetingRepository meetingRepository;
    private UserContextService userContextService;
    private OrganisationService organisationService;
    private ParticipantService participantService;


    @BeforeEach
    public void setup() {
        participantRepository = Mockito.mock(ParticipantRepository.class);
        meetingUserService = Mockito.mock(MeetingUserService.class);
        meetingRepository = Mockito.mock(MeetingRepository.class);
        userContextService = Mockito.mock(UserContextService.class);
        organisationService = Mockito.mock(OrganisationService.class);
        participantService = new ParticipantServiceImpl(participantRepository, meetingRepository, userContextService, meetingUserService, organisationService);
    }

    private void setupValidUserContext(Organisation organisation) throws PermissionDeniedException {
        var userContext = Mockito.mock(UserContext.class);
        Mockito.when(userContext.hasOnlyRole(UserRole.PROVISIONER)).thenReturn(false);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);
        Mockito.when(organisationService.getUserOrganisation()).thenReturn(organisation);
    }

    private dk.medcom.video.api.dao.entity.Meeting createMeeting(UUID uuid, Organisation organisation) {
        var meeting = new dk.medcom.video.api.dao.entity.Meeting();
        meeting.setUuid(uuid.toString());
        meeting.setOrganisation(organisation);
        return meeting;
    }

    @Test
    public void testGetParticipants() throws PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var meeting = createMeeting(uuid, new Organisation());
        setupValidUserContext(meeting.getOrganisation());
        var participants = List.of(new Participant(), new Participant());
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantRepository.findByMeeting(meeting)).thenReturn(participants);

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
    public void testGetParticipantsOrganisationMismatch() throws PermissionDeniedException {
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
    public void testCreateParticipants() throws PermissionDeniedException {
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        var createParticipants = List.of(
                new CreateParticipantModel(ParticipantType.CITIZEN, "ext-id", "org", ParticipantRole.GUEST)
        );
        var savedParticipant = new Participant();
        setupValidUserContext(organisation);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantRepository.save(Mockito.any())).thenReturn(savedParticipant);

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
    public void testCreateParticipantsOrganisationMismatch() throws PermissionDeniedException {
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
    void testUpdateParticipant() throws PermissionDeniedException{
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        setupValidUserContext(organisation);
        var updateParticipant = new UpdateParticipantModel(ParticipantRole.GUEST);
        var savedParticipant = new Participant();
        savedParticipant.setMeeting(meeting);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantRepository.findById(Mockito.any())).thenReturn(Optional.of(savedParticipant));
        Mockito.when(participantRepository.save(Mockito.any())).thenReturn(savedParticipant);
        var result = participantService.updateParticipant(uuid, 1L, updateParticipant);
        Mockito.verify(participantRepository).save(savedParticipant);
        assertEquals(result.role(), updateParticipant.role());
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
    void testUpdateParticipantOrganisationMisMatch()  throws PermissionDeniedException{

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
    void testDeleteParticipant() throws PermissionDeniedException  {
        var uuid = UUID.randomUUID();
        var organisation = new Organisation();
        var meeting = createMeeting(uuid, organisation);
        setupValidUserContext(organisation);
        var participantToDelete = new Participant();
        participantToDelete.setMeeting(meeting);
        Mockito.when(meetingRepository.findOneByUuid(uuid.toString())).thenReturn(meeting);
        Mockito.when(participantRepository.findById(Mockito.any())).thenReturn(Optional.of(participantToDelete));
        Mockito.when(participantRepository.save(Mockito.any())).thenReturn(participantToDelete);
        participantService.deleteParticipant(uuid, participantToDelete.getId());

        Mockito.verify(participantRepository).delete(participantToDelete);
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
    void testDeleteParticipantOrganisationMismatch() throws PermissionDeniedException {
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
