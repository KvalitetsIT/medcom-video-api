package dk.medcom.video.api.service;

import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.Meeting;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.*;
import dk.medcom.video.api.helper.TestDataHelper;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.*;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class SchedulingInfoServiceTest {
    private SchedulingInfoRepository schedulingInfoRepository;
    private OrganisationRepository organizationRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;

    private UUID schedulingInfoUuid;

    private UUID reservationId;

    private MeetingUserService meetingUserService;
    private SchedulingTemplateService schedulingTemplateService;

    private static final String NON_POOL_ORG = "nonPoolOrg";
    private static final String POOL_ORG = "poolOrg";

    private static final long SCHEDULING_TEMPLATE_ID = 1L;
    private static final long SCHEDULING_TEMPLATE_ID_OTHER_ORG = 2L;

    private SchedulingTemplate schedulingTemplateIdOne;
    private SchedulingStatusService schedulingStatusService;
    private OrganisationStrategy organisationStrategy;
    private UserContextService userContextService;

    @Before
    public void setupMocks() throws RessourceNotFoundException, PermissionDeniedException {
        reservationId = UUID.randomUUID();

        schedulingTemplateIdOne = createSchedulingTemplate(SCHEDULING_TEMPLATE_ID);

        schedulingInfoUuid = UUID.randomUUID();
        SchedulingInfo schedulingInfo = createSchedulingInfo();
        
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        Mockito.when(schedulingInfoRepository.findOneByUuid(schedulingInfoUuid.toString())).thenReturn(schedulingInfo);
        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).then(i -> i.getArgument(0));
        Mockito.when(schedulingInfoRepository.findOneByReservationId(reservationId.toString())).thenReturn(schedulingInfo);
        BigInteger id = new BigInteger("123");

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(Mockito.any(Long.class), Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()))).thenReturn(Collections.singletonList(id));
        Mockito.when(schedulingInfoRepository.findById(Mockito.eq(123L))).thenReturn(Optional.of(schedulingInfo));
        meetingUserService = Mockito.mock(MeetingUserService.class);

        organizationRepository = Mockito.mock(OrganisationRepository.class);
        Mockito.when(organizationRepository.findByOrganisationId(NON_POOL_ORG)).thenReturn(createNonPoolOrganisation());
        Mockito.when(organizationRepository.findByOrganisationId(POOL_ORG)).thenReturn(createOrganisation());

        schedulingTemplateService = Mockito.mock(SchedulingTemplateService.class);
        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(SCHEDULING_TEMPLATE_ID)).thenReturn(schedulingTemplateIdOne);

        schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        Mockito.when(schedulingTemplateRepository.findById(SCHEDULING_TEMPLATE_ID)).thenReturn(Optional.of(schedulingTemplateIdOne));
        Mockito.when(schedulingTemplateRepository.findById(SCHEDULING_TEMPLATE_ID_OTHER_ORG)).thenReturn(Optional.of(createSchedulingTemplateOtherOrg()));

        schedulingStatusService = Mockito.mock(SchedulingStatusService.class);

        organisationStrategy = Mockito.mock(OrganisationStrategy.class);
        Mockito.when(organisationStrategy.findOrganisationByCode(NON_POOL_ORG)).thenReturn(createNonPoolStrategyOrganisation());
        Mockito.when(organisationStrategy.findOrganisationByCode(POOL_ORG)).thenReturn(createStrategyOrganisation());

        userContextService = Mockito.mock(UserContextService.class);
    }



    @Test(expected = RessourceNotFoundException.class)
    public void testUpdateSchedulingInfoNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, null, null, organizationRepository, null, userContextService);

        schedulingInfoService.updateSchedulingInfo(UUID.randomUUID().toString(), new Date());
    }

    @Test
    public void testUpdateSchedulingInfo() throws RessourceNotFoundException, PermissionDeniedException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, null, userContextService);

        SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(schedulingInfoUuid.toString(), startTime);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertEquals(calculatedStartTime, capturedSchedulingInfo.getvMRStartTime());
        assertEquals("null/?url=null&pin=&start_dato=2019-10-10T09:00:00", capturedSchedulingInfo.getPortalLink());
    }

    @Test
    public void testUpdateSchedulingInfoDeProvision() throws RessourceNotFoundException, PermissionDeniedException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService);

        UpdateSchedulingInfoDto input = new UpdateSchedulingInfoDto();
        input.setProvisionStatus(ProvisionStatus.DEPROVISION_OK);
        input.setProvisionStatusDescription("OK");
        input.setProvisionVmrId("vmr");

        SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(schedulingInfoUuid.toString(), input);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertNull(capturedSchedulingInfo.getUriWithoutDomain());
    }

    @Test
    public void testUpdateSchedulingInfoProvisionedOk() throws RessourceNotFoundException, PermissionDeniedException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService);

        UpdateSchedulingInfoDto input = new UpdateSchedulingInfoDto();
        input.setProvisionStatus(ProvisionStatus.PROVISIONED_OK);
        input.setProvisionStatusDescription("OK");
        input.setProvisionVmrId("vmr");

        SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(schedulingInfoUuid.toString(), input);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertNotNull(capturedSchedulingInfo.getUriWithoutDomain());
    }


    @Test
    public void testCreateSchedulingInfoPooling() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();

        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(POOL_ORG);
        input.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);
        SchedulingInfo schedulingInfo = schedulingInfoService.createSchedulingInfo(input);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertNull(capturedSchedulingInfo.getvMRStartTime());
        assertEquals("some theme", capturedSchedulingInfo.getIvrTheme());
        assertNull(capturedSchedulingInfo.getPortalLink());
        assertEquals(createOrganisation().getId(), capturedSchedulingInfo.getOrganisation().getId());
        assertNull(capturedSchedulingInfo.getProvisionVMRId());
//        assertEquals("", capturedSchedulingInfo.getProvisionTimestamp());
        assertNull(capturedSchedulingInfo.getProvisionTimestamp());
        assertEquals("Pooled awaiting provisioning.", capturedSchedulingInfo.getProvisionStatusDescription());
        assertEquals(ProvisionStatus.AWAITS_PROVISION, capturedSchedulingInfo.getProvisionStatus());
        assertEquals(1, capturedSchedulingInfo.getSchedulingTemplate().getId().intValue());
        assertNotNull(capturedSchedulingInfo.getUriWithoutDomain());
        assertEquals(schedulingTemplateIdOne.getUriPrefix() + capturedSchedulingInfo.getUriWithoutDomain() + '@' + schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriWithDomain());
        assertNotNull(capturedSchedulingInfo.getUriWithDomain());
        assertTrue(capturedSchedulingInfo.getEndMeetingOnEndTime());
        assertEquals(10, capturedSchedulingInfo.getMaxParticipants());
        assertEquals(10, capturedSchedulingInfo.getVMRAvailableBefore());
        assertNotNull(capturedSchedulingInfo.getGuestPin());
        assertNotNull(capturedSchedulingInfo.getHostPin());
    }

    @Test
    public void testCreateSchedulingInfoMeeting() throws PermissionDeniedException, NotAcceptableException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();

        Meeting meeting = new Meeting();
        meeting.setStartTime(new Date());

        CreateMeetingDto createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);
        SchedulingInfo schedulingInfo = schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertTrue("Host pin should be greater than 0.", capturedSchedulingInfo.getHostPin() > 0);
        assertTrue("Guest pin should be greater than 0.", capturedSchedulingInfo.getGuestPin() > 0);
        assertNotNull(capturedSchedulingInfo.getUriWithoutDomain());

        assertEquals(schedulingTemplateIdOne.getUriPrefix() + capturedSchedulingInfo.getUriWithoutDomain() + '@' + schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriWithDomain());
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonExistingSchedulingTemplate() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(POOL_ORG);
        input.setSchedulingTemplateId(10L);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnSchedulingTemplateForOtherOrg() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(POOL_ORG);
        input.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID_OTHER_ORG);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test
    public void testAttachMeetingToSchedulingInfo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date vmrStartTime = calendar.getTime();

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertFalse(result.getPoolOverflow());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoOverflowPool() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date vmrStartTime = calendar.getTime();

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());
        meeting.getOrganisation().setOrganisationId("some_other_id");

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertTrue(result.getPoolOverflow());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoNoFreePool() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(Mockito.any(), Mockito.any())).thenReturn(null);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

        assertNull(result);
    }


    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonPoolOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(NON_POOL_ORG);
        input.setSchedulingTemplateId(2L);

        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService);

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonExistingOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId("non existing org");
        input.setSchedulingTemplateId(2L);

        SchedulingInfoService schedulingInfoService = new SchedulingInfoService(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService);

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test
    public void testGetUnusedSchedulingInfoForOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setId(1234L);
        organisation.setName("this is org name");
        organisation.setOrganisationId("RH");
        organisation.setPoolSize(10);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(), ProvisionStatus.PROVISIONED_OK.name())).thenReturn(Collections.singletonList(BigInteger.ONE));

        Long schedulingInfo = schedulingInfoService.getUnusedSchedulingInfoForOrganisation(organisation);
        assertNotNull(schedulingInfo);
    }

    @Test
    public void testGetUnusedSchedulingInfoForOrganisationNoMoreUnused() {
        Organisation organisation = new Organisation();
        organisation.setId(1234L);
        organisation.setName("this is org name");
        organisation.setOrganisationId("RH");
        organisation.setPoolSize(10);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(organisation.getId(), ProvisionStatus.PROVISIONED_OK.name())).thenReturn(Collections.emptyList());

        Long schedulingInfo = schedulingInfoService.getUnusedSchedulingInfoForOrganisation(organisation);
        assertNull(schedulingInfo);
    }

    @Test
    public void testAttachMeetingToSchedulingInfoMicrophoneOff() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date vmrStartTime = calendar.getTime();

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());
        meeting.setGuestMicrophone(GuestMicrophone.off);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00&microphone=off", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoMicrophoneMuted() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date vmrStartTime = calendar.getTime();

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());
        meeting.setGuestMicrophone(GuestMicrophone.muted);

        SchedulingInfoService schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00&microphone=muted", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
    }

    @Test
    public void testReserveSchedulingInfo() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.reserveSchedulingInfo();
        assertNotNull(result);

        Mockito.verify(organizationRepository, times(1)).findByOrganisationId("poolOrg");
        Mockito.verify(schedulingInfoRepository, times(1)).findByMeetingIsNullAndOrganisationAndProvisionStatus(1L, "PROVISIONED_OK");
        var schedulingInfoCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoCaptor.capture());
        assertNotNull(schedulingInfoCaptor.getValue());
        var schedulingInfo = schedulingInfoCaptor.getValue();
        assertNotNull(schedulingInfo.getReservationId());
    }

    @Test(expected = RessourceNotFoundException.class)
    public void testReserveSchedulingInfoNoFree() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Mockito.reset(schedulingInfoRepository);
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.reserveSchedulingInfo();
    }

    @Test
    public void testGetSchedulingInfoByReservation() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.getSchedulingInfoByReservation(reservationId);
        assertNotNull(result);

        Mockito.verify(schedulingInfoRepository, times(1)).findOneByReservationId(reservationId.toString());
    }

    @Test(expected = RessourceNotFoundException.class)
    public void testGetSchedulingInfoByReservationNotFound() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.getSchedulingInfoByReservation(UUID.randomUUID());
    }

    private SchedulingTemplate createSchedulingTemplate(long id) {
        SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
        schedulingTemplate.setId(id);
        schedulingTemplate.setUriNumberRangeLow(1000L);
        schedulingTemplate.setUriNumberRangeHigh(2000L);
        schedulingTemplate.setUriPrefix("uri_prefix");
        schedulingTemplate.setUriDomain("test_domain");
        schedulingTemplate.setVMRAvailableBefore(10);
        schedulingTemplate.setMaxParticipants(10);
        schedulingTemplate.setIvrTheme("some theme");
        schedulingTemplate.setHostPinRequired(true);
        schedulingTemplate.setHostPinRangeLow(10000L);
        schedulingTemplate.setHostPinRangeHigh(20000L);
        schedulingTemplate.setGuestPinRequired(true);
        schedulingTemplate.setGuestPinRangeLow(4000L);
        schedulingTemplate.setGuestPinRangeHigh(5000L);
        schedulingTemplate.setEndMeetingOnEndTime(true);
        schedulingTemplate.setConferencingSysId(1111L);
        schedulingTemplate.setIsDefaultTemplate(false);

        return schedulingTemplate;
    }

    private SchedulingTemplate createSchedulingTemplateOtherOrg() {

        SchedulingTemplate schedulingTemplate = createSchedulingTemplate(SCHEDULING_TEMPLATE_ID_OTHER_ORG);
        schedulingTemplate.setOrganisation(createOrganisation(false, "some org id", 10L));

        return schedulingTemplate;
    }

    private SchedulingInfo createSchedulingInfo() {
        SchedulingInfo schedulingInfo = new SchedulingInfo();
        schedulingInfo.setVMRAvailableBefore(10);
        schedulingInfo.setUuid(schedulingInfoUuid.toString());
        schedulingInfo.setOrganisation(createOrganisation());
        schedulingInfo.setUriWithoutDomain("random_uri");
        schedulingInfo.setMeeting(new Meeting());

        return schedulingInfo;
    }

    private SchedulingInfoService createSchedulingInfoService() {
        return new SchedulingInfoService(schedulingInfoRepository, schedulingTemplateRepository, schedulingTemplateService, null, meetingUserService, organizationRepository, organisationStrategy, userContextService);
    }

    private Organisation createNonPoolOrganisation()  {
        return createOrganisation(false, NON_POOL_ORG, 2);
    }

    private Organisation createOrganisation(boolean poolEnabled, String orgId, long id)  {
        return TestDataHelper.createOrganisation(poolEnabled, orgId, id);
    }

    private dk.medcom.video.api.organisation.Organisation createNonPoolStrategyOrganisation() {
        dk.medcom.video.api.organisation.Organisation organisation = new dk.medcom.video.api.organisation.Organisation();
        organisation.setPoolSize(null);
        organisation.setCode(NON_POOL_ORG);

        return organisation;
    }

    private dk.medcom.video.api.organisation.Organisation createStrategyOrganisation() {
        dk.medcom.video.api.organisation.Organisation organisation = new dk.medcom.video.api.organisation.Organisation();
        organisation.setPoolSize(10);
        organisation.setCode(POOL_ORG);

        return organisation;
    }

    private Organisation createOrganisation() {
        return createOrganisation(true, POOL_ORG, 1);
    }
}
