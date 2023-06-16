package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.*;
import dk.medcom.video.api.context.UserContext;
import dk.medcom.video.api.context.UserContextImpl;
import dk.medcom.video.api.context.UserContextService;
import dk.medcom.video.api.context.UserRole;
import dk.medcom.video.api.controller.exceptions.*;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.*;
import dk.medcom.video.api.helper.TestDataHelper;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.*;

import static dk.medcom.video.api.helper.TestDataHelper.createMeetingUser;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public class SchedulingInfoServiceImplTest {
    private static final String OVERFLOW_POOL = "overflow";
    private SchedulingInfoRepository schedulingInfoRepository;
    private OrganisationRepository organizationRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;

    private UUID schedulingInfoUuid;

    private UUID reservationId;

    private MeetingUserService meetingUserService;
    private SchedulingTemplateServiceImpl schedulingTemplateService;

    private static final String NON_POOL_ORG = "nonPoolOrg";
    private static final String POOL_ORG = "poolOrg";

    private static final long SCHEDULING_TEMPLATE_ID = 1L;
    private static final long SCHEDULING_TEMPLATE_ID_OTHER_ORG = 2L;

    private SchedulingTemplate schedulingTemplateIdOne;
    private SchedulingStatusServiceImpl schedulingStatusService;
    private OrganisationStrategy organisationStrategy;
    private UserContextService userContextService;
    private OrganisationTreeServiceClient organisationTreeServiceClient;
    private AuditService auditService;
    private SchedulingInfoEventPublisher schedulingInfoEventPublisher;
    private PoolFinderService poolFinderService;

    @Before
    public void setupMocks() throws RessourceNotFoundException, PermissionDeniedException {
        auditService = Mockito.mock(AuditService.class);

        organisationTreeServiceClient = Mockito.mock(OrganisationTreeServiceClient.class);
        reservationId = UUID.randomUUID();

        schedulingTemplateIdOne = createSchedulingTemplate(SCHEDULING_TEMPLATE_ID);

        schedulingInfoUuid = UUID.randomUUID();
        SchedulingInfo schedulingInfo = createSchedulingInfo();
        
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        Mockito.when(schedulingInfoRepository.findOneByUuid(schedulingInfoUuid.toString())).thenReturn(schedulingInfo);
        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).then(i -> i.getArgument(0));
        Mockito.when(schedulingInfoRepository.findOneByReservationId(reservationId.toString())).thenReturn(schedulingInfo);

        Mockito.when(schedulingInfoRepository.findById(Mockito.eq(123L))).thenReturn(Optional.of(schedulingInfo));
        meetingUserService = Mockito.mock(MeetingUserService.class);

        organizationRepository = Mockito.mock(OrganisationRepository.class);
        Mockito.when(organizationRepository.findByOrganisationId(NON_POOL_ORG)).thenReturn(createNonPoolOrganisation());
        Mockito.when(organizationRepository.findById(createNonPoolOrganisation().getId())).thenReturn(Optional.of(createNonPoolOrganisation()));
        Mockito.when(organizationRepository.findByOrganisationId(POOL_ORG)).thenReturn(createOrganisation());
        Mockito.when(organizationRepository.findById(createOrganisation().getId())).thenReturn(Optional.of(createOrganisation()));
        Mockito.when(organizationRepository.findByOrganisationId(OVERFLOW_POOL)).thenReturn(createOverflowPool());

        schedulingTemplateService = Mockito.mock(SchedulingTemplateServiceImpl.class);
        Mockito.when(schedulingTemplateService.getSchedulingTemplateFromOrganisationAndId(SCHEDULING_TEMPLATE_ID)).thenReturn(schedulingTemplateIdOne);

        schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        Mockito.when(schedulingTemplateRepository.findById(SCHEDULING_TEMPLATE_ID)).thenReturn(Optional.of(schedulingTemplateIdOne));
        Mockito.when(schedulingTemplateRepository.findById(SCHEDULING_TEMPLATE_ID_OTHER_ORG)).thenReturn(Optional.of(createSchedulingTemplateOtherOrg()));

        schedulingStatusService = Mockito.mock(SchedulingStatusServiceImpl.class);

        organisationStrategy = Mockito.mock(OrganisationStrategy.class);
        Mockito.when(organisationStrategy.findOrganisationByCode(NON_POOL_ORG)).thenReturn(createNonPoolStrategyOrganisation());
        Mockito.when(organisationStrategy.findOrganisationByCode(POOL_ORG)).thenReturn(createStrategyOrganisation());

        userContextService = Mockito.mock(UserContextService.class);

        schedulingInfoEventPublisher = Mockito.mock(SchedulingInfoEventPublisher.class);

        poolFinderService = Mockito.mock(PoolFinderService.class);
    }

    @Test(expected = RessourceNotFoundException.class)
    public void testUpdateSchedulingInfoNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository,
                null,
                null,
                null,
                null,
                organizationRepository,
                null,
                userContextService,
                "overflow",
                organisationTreeServiceClient,
                auditService,
                new CustomUriValidatorImpl(),
                schedulingInfoEventPublisher,
                null,
                null);

        schedulingInfoService.updateSchedulingInfo(UUID.randomUUID().toString(), new Date(), 12345L, 2341L);
    }

    @Test
    public void testUpdateSchedulingInfo() throws RessourceNotFoundException, PermissionDeniedException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        var hostPin = 1234L;
        var guestPin = 4321L;

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

        SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(schedulingInfoUuid.toString(), startTime, hostPin, guestPin);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertEquals(calculatedStartTime, capturedSchedulingInfo.getvMRStartTime());
        assertEquals("null/?url=null&pin=&start_dato=2019-10-10T09:00:00", capturedSchedulingInfo.getPortalLink());
        assertEquals(hostPin, capturedSchedulingInfo.getHostPin().longValue());
        assertEquals(guestPin, capturedSchedulingInfo.getGuestPin().longValue());

        Mockito.verify(auditService, times(1)).auditSchedulingInformation(expectedSchedulingInfo, "update");
    }

    @Test
    public void testUpdateSchedulingInfoNullPin() throws RessourceNotFoundException, PermissionDeniedException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

        SchedulingInfo schedulingInfo = schedulingInfoService.updateSchedulingInfo(schedulingInfoUuid.toString(), startTime, null, null);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertEquals(calculatedStartTime, capturedSchedulingInfo.getvMRStartTime());
        assertEquals("null/?url=null&pin=&start_dato=2019-10-10T09:00:00", capturedSchedulingInfo.getPortalLink());
        assertNull(capturedSchedulingInfo.getHostPin());
        assertNull(capturedSchedulingInfo.getGuestPin());

        Mockito.verify(auditService, times(1)).auditSchedulingInformation(expectedSchedulingInfo, "update");
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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

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
        assertNull(capturedSchedulingInfo.getUriDomain());
        Mockito.verify(auditService, times(1)).auditSchedulingInformation(expectedSchedulingInfo, "update");
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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

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
        assertNotNull(capturedSchedulingInfo.getUriDomain());
        Mockito.verify(auditService, times(1)).auditSchedulingInformation(expectedSchedulingInfo, "update");
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

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();

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
        assertEquals(schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriDomain());
        assertTrue(capturedSchedulingInfo.getEndMeetingOnEndTime());
        assertEquals(10, capturedSchedulingInfo.getMaxParticipants());
        assertEquals(10, capturedSchedulingInfo.getVMRAvailableBefore());
        assertNotNull(capturedSchedulingInfo.getGuestPin());
        assertNotNull(capturedSchedulingInfo.getHostPin());
        assertEquals(schedulingTemplateIdOne.getCustomPortalGuest(), capturedSchedulingInfo.getCustomPortalGuest());
        assertEquals(schedulingTemplateIdOne.getCustomPortalHost(), capturedSchedulingInfo.getCustomPortalHost());
        assertEquals(schedulingTemplateIdOne.getReturnUrl(), capturedSchedulingInfo.getReturnUrl());
        assertEquals(DirectMedia.best_effort, capturedSchedulingInfo.getDirectMedia());

        Mockito.verify(schedulingInfoEventPublisher, times(1)).publishCreate(Mockito.any());
    }

    @Test
    public void testCreateSchedulingInfoMeeting() throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();

        Meeting meeting = new Meeting();
        meeting.setStartTime(new Date());

        CreateMeetingDto createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);
        createMeetingDto.setDirectMedia(DirectMedia.best_effort);
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
        assertEquals(schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriDomain());
        assertEquals(VmrType.conference, capturedSchedulingInfo.getVmrType());
        assertEquals(ViewType.one_main_seven_pips, capturedSchedulingInfo.getHostView());
        assertEquals(ViewType.two_mains_twentyone_pips, capturedSchedulingInfo.getGuestView());
        assertEquals(VmrQuality.fullhd, capturedSchedulingInfo.getVmrQuality());
        assertTrue(capturedSchedulingInfo.getEnableOverlayText());
        assertTrue(capturedSchedulingInfo.getGuestsCanPresent());
        assertTrue(capturedSchedulingInfo.getForcePresenterIntoMain());
        assertFalse(capturedSchedulingInfo.getForceEncryption());
        assertFalse(capturedSchedulingInfo.getMuteAllGuests());
        assertEquals(schedulingTemplateIdOne.getCustomPortalGuest(), capturedSchedulingInfo.getCustomPortalGuest());
        assertEquals(schedulingTemplateIdOne.getCustomPortalHost(), capturedSchedulingInfo.getCustomPortalHost());
        assertEquals(schedulingTemplateIdOne.getReturnUrl(), capturedSchedulingInfo.getReturnUrl());
        assertEquals(createMeetingDto.getDirectMedia(), capturedSchedulingInfo.getDirectMedia());
    }

    @Test
    public void testCreateSchedulingInfoMeetingCustomUriWithDomainAndPin() throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        var customUriValidator = Mockito.mock(CustomUriValidator.class);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService(customUriValidator);

        Meeting meeting = new Meeting();
        meeting.setStartTime(new Date());

        CreateMeetingDto createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setUriWithoutDomain("573489");
        createMeetingDto.setHostPin(1234);
        createMeetingDto.setGuestPin(4321);
        createMeetingDto.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);
        SchedulingInfo schedulingInfo = schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);

        assertNotNull(schedulingInfo);
        assertEquals(calculatedStartTime, schedulingInfo.getvMRStartTime());

        ArgumentCaptor<SchedulingInfo> schedulingInfoServiceArgumentCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoServiceArgumentCaptor.capture());
        SchedulingInfo capturedSchedulingInfo = schedulingInfoServiceArgumentCaptor.getValue();

        assertEquals(createMeetingDto.getHostPin().longValue(), capturedSchedulingInfo.getHostPin().longValue());
        assertEquals(createMeetingDto.getGuestPin().longValue(), capturedSchedulingInfo.getGuestPin().longValue());
        assertNotNull(capturedSchedulingInfo.getUriWithoutDomain());

        assertEquals(capturedSchedulingInfo.getUriWithoutDomain() + '@' + schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriWithDomain());
        assertEquals(schedulingTemplateIdOne.getUriDomain(), capturedSchedulingInfo.getUriDomain());
        assertEquals(VmrType.conference, capturedSchedulingInfo.getVmrType());
        assertEquals(ViewType.one_main_seven_pips, capturedSchedulingInfo.getHostView());
        assertEquals(ViewType.two_mains_twentyone_pips, capturedSchedulingInfo.getGuestView());
        assertEquals(VmrQuality.fullhd, capturedSchedulingInfo.getVmrQuality());
        assertTrue(capturedSchedulingInfo.getEnableOverlayText());
        assertTrue(capturedSchedulingInfo.getGuestsCanPresent());
        assertTrue(capturedSchedulingInfo.getForcePresenterIntoMain());
        assertFalse(capturedSchedulingInfo.getForceEncryption());
        assertFalse(capturedSchedulingInfo.getMuteAllGuests());

        Mockito.verify(schedulingInfoRepository, times(1)).findOneByUriWithoutDomainAndUriDomain(createMeetingDto.getUriWithoutDomain(),schedulingTemplateIdOne.getUriDomain());
        Mockito.verify(customUriValidator, times(1)).validate(createMeetingDto.getUriWithoutDomain());
    }

    @Test
    public void testCreateSchedulingInfoMeetingCustomUriWithDomainAlreadyUsed() throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        var customUriValidator = Mockito.mock(CustomUriValidator.class);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService(customUriValidator);

        Meeting meeting = new Meeting();
        meeting.setStartTime(new Date());

        CreateMeetingDto createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setUriWithoutDomain("573489");
        createMeetingDto.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);

        Mockito.when(schedulingInfoRepository.findOneByUriWithoutDomainAndUriDomain(createMeetingDto.getUriWithoutDomain(), schedulingTemplateIdOne.getUriDomain())).thenReturn(new SchedulingInfo());

        try {
            schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
            fail();
        }
        catch(NotValidDataException e) {
            assertEquals(90, e.getErrorCode());
        }

        Mockito.verify(schedulingInfoRepository, times(0)).save(Mockito.any());

        Mockito.verify(schedulingInfoRepository, times(1)).findOneByUriWithoutDomainAndUriDomain(createMeetingDto.getUriWithoutDomain(), schedulingTemplateIdOne.getUriDomain());
        Mockito.verify(customUriValidator, times(1)).validate(createMeetingDto.getUriWithoutDomain());
    }

    @Test
    public void testCreateSchedulingInfoMeetingCustomUriWithDomainValidationError() throws PermissionDeniedException, NotAcceptableException, NotValidDataException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 10, 9, 0, 0);
        calendar.add(Calendar.MINUTE, -10);
        Date calculatedStartTime = calendar.getTime();

        SchedulingInfo expectedSchedulingInfo = createSchedulingInfo();
        expectedSchedulingInfo.setvMRStartTime(calculatedStartTime);

        Mockito.when(schedulingInfoRepository.save(Mockito.any(SchedulingInfo.class))).thenReturn(expectedSchedulingInfo);

        var customUriValidator = Mockito.mock(CustomUriValidator.class);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService(customUriValidator);

        Meeting meeting = new Meeting();
        meeting.setStartTime(new Date());

        CreateMeetingDto createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setUriWithoutDomain("573 489");
        createMeetingDto.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID);

        Mockito.doThrow(new NotValidDataException(NotValidDataErrors.URI_IS_INVALID)).when(customUriValidator).validate(createMeetingDto.getUriWithoutDomain());

        try {
            schedulingInfoService.createSchedulingInfo(meeting, createMeetingDto);
            fail();
        }
        catch(NotValidDataException e) {
            assertEquals(100, e.getErrorCode());
        }

        Mockito.verify(schedulingInfoRepository, times(0)).save(Mockito.any());
        Mockito.verify(schedulingInfoRepository, times(0)).findOneByUriWithoutDomainAndUriDomain(createMeetingDto.getUriWithoutDomain(), null);
        Mockito.verify(customUriValidator, times(1)).validate(createMeetingDto.getUriWithoutDomain());
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonExistingSchedulingTemplate() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(POOL_ORG);
        input.setSchedulingTemplateId(10L);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnSchedulingTemplateForOtherOrg() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(POOL_ORG);
        input.setSchedulingTemplateId(SCHEDULING_TEMPLATE_ID_OTHER_ORG);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test
    public void testAttachMeetingToSchedulingInfo() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var organisation = createOrganisation();
        Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(createMeetingUser(organisation));

        var poolSchedulingInfo = new SchedulingInfo();
        poolSchedulingInfo.setId(123L);
        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any())).thenReturn(Optional.of(poolSchedulingInfo));

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
        meeting.setMeetingUser(createMeetingUser(createOrganisation()));
        meeting.setEndTime(new Date());

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertFalse(result.getPoolOverflow());
        assertEquals(meeting.getOrganisation().getOrganisationId(), result.getOrganisation().getOrganisationId());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoOverflowPool() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(createMeetingUser(createOrganisation()));

        UserContext userContext = new UserContextImpl(POOL_ORG, "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setId(123L);

        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x != null && x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x != null && x.getOrganisationId().equalsIgnoreCase(OVERFLOW_POOL)), Mockito.any())).thenReturn(Optional.of(schedulingInfo));

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
        meeting.setMeetingUser(createMeetingUser(createOrganisation()));
        meeting.setEndTime(new Date());

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any());
        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(OVERFLOW_POOL)), Mockito.any());

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertTrue(result.getPoolOverflow());
        assertEquals(meeting.getOrganisation().getOrganisationId(), result.getOrganisation().getOrganisationId());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoNoFreePool() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        UserContext userContext = new UserContextImpl(POOL_ORG, "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createOrganisation());

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any());
        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(OVERFLOW_POOL)), Mockito.any());

        assertNull(result);
    }

    @Test
    public void testAttachMeetingToSchedulingInfoNoPoolOrganisation() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        UserContext userContext = new UserContextImpl(NON_POOL_ORG, "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 7, 12, 0, 0);
        Date startTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, -10);

        Meeting meeting = new Meeting();
        meeting.setStartTime(startTime);
        meeting.setId(1L);
        meeting.setUuid(UUID.randomUUID().toString());
        meeting.setOrganisation(createNonPoolOrganisation());
        meeting.getOrganisation().setPoolSize(null);

        OrganisationTree organisationTree = new OrganisationTree();
        organisationTree.setPoolSize(0);
        organisationTree.setCode(NON_POOL_ORG);
        organisationTree.setName("nonPoolOrg name");

        Mockito.when(organisationTreeServiceClient.getOrganisationTree(NON_POOL_ORG)).thenReturn(organisationTree);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(NON_POOL_ORG)), Mockito.any());

        Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree(NON_POOL_ORG);

        assertNull(result);
    }


    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonPoolOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(NON_POOL_ORG);
        input.setSchedulingTemplateId(2L);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonExistingOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId("non existing org");
        input.setSchedulingTemplateId(2L);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl(), schedulingInfoEventPublisher, null, null);

        schedulingInfoService.createSchedulingInfo(input);
        Mockito.verifyNoMoreInteractions(auditService);
    }

//    @Test
//    public void testGetUnusedSchedulingInfoForOrganisation() {
//        Organisation organisation = new Organisation();
//        organisation.setId(1234L);
//        organisation.setName("this is org name");
//        organisation.setOrganisationId("RH");
//        organisation.setPoolSize(10);
//
//        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
//        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
//                Mockito.eq(organisation.getId()),
//                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any())).thenReturn(Collections.singletonList(BigInteger.ONE));
//
//        Long schedulingInfo = schedulingInfoService.getUnusedSchedulingInfoForOrganisation(organisation, null);
//        assertNotNull(schedulingInfo);
//    }
//
//    @Test
//    public void testGetUnusedSchedulingInfoForOrganisationNoMoreUnused() {
//        Organisation organisation = new Organisation();
//        organisation.setId(1234L);
//        organisation.setName("this is org name");
//        organisation.setOrganisationId("RH");
//        organisation.setPoolSize(10);
//
//        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
//        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
//                Mockito.eq(organisation.getId()),
//                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any(),
//                Mockito.any())).thenReturn(Collections.emptyList());
//
//        Long schedulingInfo = schedulingInfoService.getUnusedSchedulingInfoForOrganisation(organisation, null);
//        assertNull(schedulingInfo);
//    }

    @Test
    public void testAttachMeetingToSchedulingInfoMicrophoneOff() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(createMeetingUser(createOrganisation()));

        var schedulingInfo = createSchedulingInfo();
        schedulingInfo.setId(123L);
        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any())).thenReturn(Optional.of(schedulingInfo));

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
        meeting.setMeetingUser(createMeetingUser(createOrganisation()));
        meeting.setEndTime(new Date());

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00&microphone=off", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoMicrophoneMuted() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        Mockito.when(meetingUserService.getOrCreateCurrentMeetingUser()).thenReturn(createMeetingUser(createOrganisation()));

        var schedulingInfo = createSchedulingInfo();
        schedulingInfo.setId(123L);
        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any())).thenReturn(Optional.of(schedulingInfo));

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
        meeting.setMeetingUser(createMeetingUser(createOrganisation()));
        meeting.setEndTime(new Date());

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00&microphone=muted", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
    }

    @Test
    public void testReserveSchedulingInfo() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setId(123L);
        Mockito.when(poolFinderService.findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any())).thenReturn(Optional.of(schedulingInfo));

        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_zero_pips,
                ViewType.four_mains_zero_pips,
                VmrQuality.fullhd,
                true,
                true,
                true,
                false,
                false);
        assertNotNull(result);

        Mockito.verify(organizationRepository, times(1)).findByOrganisationId("poolOrg");
        Mockito.verify(poolFinderService, times(1)).findPoolSubject(Mockito.argThat(x -> x.getOrganisationId().equalsIgnoreCase(POOL_ORG)), Mockito.any());
        var schedulingInfoCaptor = ArgumentCaptor.forClass(SchedulingInfo.class);
        Mockito.verify(schedulingInfoRepository, times(1)).save(schedulingInfoCaptor.capture());
        assertNotNull(schedulingInfoCaptor.getValue());
        schedulingInfo = schedulingInfoCaptor.getValue();
        assertNotNull(schedulingInfo.getReservationId());
    }

    @Test(expected = RessourceNotFoundException.class)
    public void testReserveSchedulingInfoNoFree() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        Mockito.reset(schedulingInfoRepository);
        var schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.reserveSchedulingInfo(VmrType.lecture,
                ViewType.one_main_zero_pips,
                ViewType.four_mains_zero_pips,
                VmrQuality.fullhd,
                true,
                true,
                true,
                false,
                false);
    }

    @Test
    public void testGetSchedulingInfoByReservation() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.getSchedulingInfoByReservation(reservationId);
        assertNotNull(result);

        Mockito.verify(schedulingInfoRepository, times(1)).findOneByReservationId(reservationId.toString());
    }

    @Test(expected = RessourceNotFoundException.class)
    public void testGetSchedulingInfoByReservationNotFound() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN, null);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.getSchedulingInfoByReservation(UUID.randomUUID());
    }

    @Test
    public void testGetSchedulingInfoAwaitProvisionNoFilter() {
        var schedulingInfo1 = createSchedulingInfo("org1");
        var schedulingInfo2 = createSchedulingInfo("org2");
        Mockito.when(schedulingInfoRepository.findAllWithinStartAndEndTimeLessThenAndStatus(Mockito.any(), Mockito.eq(ProvisionStatus.AWAITS_PROVISION))).thenReturn(Arrays.asList(schedulingInfo1, schedulingInfo2));

        var schedulingInfoService = createSchedulingInfoService(new CustomUriValidatorImpl(), new NewProvisionerOrganisationFilterImpl(Collections.emptyList()));

        var result = schedulingInfoService.getSchedulingInfoAwaitsProvision();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSchedulingInfoAwaitProvisionFilter() {
        var schedulingInfo1 = createSchedulingInfo("org1");
        var schedulingInfo2 = createSchedulingInfo("org2");
        Mockito.when(schedulingInfoRepository.findAllWithinStartAndEndTimeLessThenAndStatus(Mockito.any(), Mockito.eq(ProvisionStatus.AWAITS_PROVISION))).thenReturn(Arrays.asList(schedulingInfo1, schedulingInfo2));

        var schedulingInfoService = createSchedulingInfoService(new CustomUriValidatorImpl(), new NewProvisionerOrganisationFilterImpl(Collections.singletonList("org1")));

        var result = schedulingInfoService.getSchedulingInfoAwaitsProvision();
        assertEquals(1, result.size());
        assertEquals(schedulingInfo2, result.get(0));
    }

    @Test
    public void testGetSchedulingInfoAwaitsDeProvisionNoFilter() {
        var schedulingInfo1 = createSchedulingInfo("org1");
        var schedulingInfo2 = createSchedulingInfo("org2");
        Mockito.when(schedulingInfoRepository.findAllWithinEndTimeLessThenAndStatus(Mockito.any(), Mockito.eq(ProvisionStatus.PROVISIONED_OK))).thenReturn(Arrays.asList(schedulingInfo1, schedulingInfo2));

        var schedulingInfoService = createSchedulingInfoService(new CustomUriValidatorImpl(), new NewProvisionerOrganisationFilterImpl(Collections.emptyList()));

        var result = schedulingInfoService.getSchedulingInfoAwaitsDeProvision();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSchedulingInfoAwaitsDeProvisionFilter() {
        var schedulingInfo1 = createSchedulingInfo("org1");
        var schedulingInfo2 = createSchedulingInfo("org2");
        Mockito.when(schedulingInfoRepository.findAllWithinEndTimeLessThenAndStatus(Mockito.any(), Mockito.eq(ProvisionStatus.PROVISIONED_OK))).thenReturn(Arrays.asList(schedulingInfo1, schedulingInfo2));

        var schedulingInfoService = createSchedulingInfoService(new CustomUriValidatorImpl(), new NewProvisionerOrganisationFilterImpl(Collections.singletonList("org1")));

        var result = schedulingInfoService.getSchedulingInfoAwaitsDeProvision();
        assertEquals(1, result.size());
        assertEquals(schedulingInfo2, result.get(0));
    }

    private SchedulingInfo createSchedulingInfo(String organisationId) {
        var organisation = new Organisation();
        organisation.setOrganisationId(organisationId);

        var schedulingInfo = new SchedulingInfo();
        schedulingInfo.setOrganisation(organisation);

        return schedulingInfo;
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
        schedulingTemplate.setVmrType(VmrType.conference);
        schedulingTemplate.setHostView(ViewType.one_main_seven_pips);
        schedulingTemplate.setGuestView(ViewType.two_mains_twentyone_pips);
        schedulingTemplate.setVmrQuality(VmrQuality.fullhd);
        schedulingTemplate.setEnableOverlayText(true);
        schedulingTemplate.setGuestsCanPresent(true);
        schedulingTemplate.setForcePresenterIntoMain(true);
        schedulingTemplate.setForceEncryption(false);
        schedulingTemplate.setMuteAllGuests(false);
        schedulingTemplate.setCustomPortalHost("some_portal_host");
        schedulingTemplate.setCustomPortalHost("some_portal_host");
        schedulingTemplate.setReturnUrl("some_return_url");
        schedulingTemplate.setVmrType(VmrType.conference);
        schedulingTemplate.setDirectMedia(DirectMedia.best_effort);

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
        schedulingInfo.setUriDomain("some_domain");
        schedulingInfo.setMeeting(new Meeting());
        schedulingInfo.getMeeting().setEndTime(new Date());
        schedulingInfo.setVmrType(VmrType.conference);
        schedulingInfo.setVmrQuality(VmrQuality.hd);
        schedulingInfo.setHostView(ViewType.one_main_zero_pips);
        schedulingInfo.setGuestView(ViewType.one_main_seven_pips);
        var meetingUser = new MeetingUser();
        meetingUser.setEmail("some_email");
        schedulingInfo.setMeetingUser(meetingUser);
        schedulingInfo.setCreatedTime(new Date());
        schedulingInfo.setSchedulingTemplate(schedulingTemplateIdOne);
        schedulingInfo.setvMRStartTime(new Date());

        return schedulingInfo;
    }

    private SchedulingInfoServiceImpl createSchedulingInfoService() {
        return createSchedulingInfoService(new CustomUriValidatorImpl());
    }

    private SchedulingInfoServiceImpl createSchedulingInfoService(CustomUriValidator customUriValidator) {
        return createSchedulingInfoService(customUriValidator, new NewProvisionerOrganisationFilterImpl(Collections.emptyList()));
    }

    private SchedulingInfoServiceImpl createSchedulingInfoService(CustomUriValidator customUriValidator, NewProvisionerOrganisationFilter excludeOrganisationsFilter) {
        return new SchedulingInfoServiceImpl(schedulingInfoRepository, schedulingTemplateRepository, schedulingTemplateService, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, OVERFLOW_POOL, organisationTreeServiceClient, auditService, customUriValidator, schedulingInfoEventPublisher, excludeOrganisationsFilter, poolFinderService);
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

    private Organisation createOverflowPool() {
        return createOrganisation(true, OVERFLOW_POOL, 3);
    }
}
