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
import dk.medcom.video.api.dao.entity.Meeting;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.helper.TestDataHelper;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.OrganisationTree;
import dk.medcom.video.api.organisation.OrganisationTreeServiceClient;
import dk.medcom.video.api.service.AuditService;
import dk.medcom.video.api.service.CustomUriValidator;
import dk.medcom.video.api.service.MeetingUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigInteger;
import java.util.*;

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
        BigInteger id = new BigInteger("123");

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(Long.class),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
                Mockito.any(), Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(String.class),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(Collections.singletonList(id));

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
    }



    @Test(expected = RessourceNotFoundException.class)
    public void testUpdateSchedulingInfoNotFound() throws RessourceNotFoundException, PermissionDeniedException {
        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, null, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, schedulingStatusService, meetingUserService, organizationRepository, null, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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
        assertEquals(VmrQuality.full_hd, capturedSchedulingInfo.getVmrQuality());
        assertTrue(capturedSchedulingInfo.getEnableOverlayText());
        assertTrue(capturedSchedulingInfo.getGuestsCanPresent());
        assertTrue(capturedSchedulingInfo.getForcePresenterIntoMain());
        assertFalse(capturedSchedulingInfo.getForceEncryption());
        assertFalse(capturedSchedulingInfo.getMuteAllGuests());
        assertEquals(schedulingTemplateIdOne.getCustomPortalGuest(), capturedSchedulingInfo.getCustomPortalGuest());
        assertEquals(schedulingTemplateIdOne.getCustomPortalHost(), capturedSchedulingInfo.getCustomPortalHost());
        assertEquals(schedulingTemplateIdOne.getReturnUrl(), capturedSchedulingInfo.getReturnUrl());
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
        assertEquals(VmrQuality.full_hd, capturedSchedulingInfo.getVmrQuality());
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

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertFalse(result.getPoolOverflow());
        assertEquals(meeting.getOrganisation().getOrganisationId(), result.getOrganisation().getOrganisationId());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoOverflowPool() {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

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

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(null).thenReturn(Collections.singletonList(new BigInteger("123")));

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(schedulingInfoRepository, times(2)).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any());

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
        assertTrue(result.getPoolOverflow());
        assertEquals(meeting.getOrganisation().getOrganisationId(), result.getOrganisation().getOrganisationId());
    }

    @Test
    public void testAttachMeetingToSchedulingInfoNoFreePool() {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
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

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(null);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(schedulingInfoRepository, times(2)).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any());

        assertNull(result);
    }

    @Test
    public void testAttachMeetingToSchedulingInfoNoPoolOrganisation() {
        UserContext userContext = new UserContextImpl(NON_POOL_ORG, "test@test.dk", UserRole.ADMIN);
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

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(null);
        Mockito.when(organisationTreeServiceClient.getOrganisationTree(NON_POOL_ORG)).thenReturn(organisationTree);

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        Mockito.verify(schedulingInfoRepository, times(1)).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any());
        Mockito.verify(organisationTreeServiceClient, times(1)).getOrganisationTree(NON_POOL_ORG);

        assertNull(result);
    }


    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonPoolOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId(NON_POOL_ORG);
        input.setSchedulingTemplateId(2L);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

        schedulingInfoService.createSchedulingInfo(input);
    }

    @Test(expected = NotValidDataException.class)
    public void testCanNotCreateSchedulingInfoOnNonExistingOrganisation() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setOrganizationId("non existing org");
        input.setSchedulingTemplateId(2L);

        SchedulingInfoServiceImpl schedulingInfoService = new SchedulingInfoServiceImpl(schedulingInfoRepository, null, null, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, "overflow", organisationTreeServiceClient, auditService, new CustomUriValidatorImpl());

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

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

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

        SchedulingInfoServiceImpl schedulingInfoService = createSchedulingInfoService();
        SchedulingInfo result = schedulingInfoService.attachMeetingToSchedulingInfo(meeting, null);

        assertNotNull(result);
        assertEquals("null/?url=null&pin=&start_dato=2019-10-07T12:00:00&microphone=muted", result.getPortalLink());
        assertEquals(vmrStartTime, result.getvMRStartTime());
    }

    @Test
    public void testReserveSchedulingInfo() throws RessourceNotFoundException {
        UserContext userContext = new UserContextImpl("poolOrg", "test@test.dk", UserRole.ADMIN);
        Mockito.when(userContextService.getUserContext()).thenReturn(userContext);

        var schedulingInfoService = createSchedulingInfoService();

        var result = schedulingInfoService.reserveSchedulingInfo(
                VmrType.lecture,
                ViewType.one_main_zero_pips,
                ViewType.four_mains_zero_pips,
                VmrQuality.full_hd,
                true,
                true,
                true,
                false,
                false);
        assertNotNull(result);

        Mockito.verify(organizationRepository, times(1)).findByOrganisationId("poolOrg");
        Mockito.verify(schedulingInfoRepository, times(1)).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(1L),
                Mockito.eq("PROVISIONED_OK"),
                Mockito.any(),
                Mockito.eq(VmrType.lecture.name()),
                Mockito.eq(ViewType.one_main_zero_pips.name()),
                Mockito.eq(ViewType.four_mains_zero_pips.name()),
                Mockito.eq(VmrQuality.full_hd.name()),
                Mockito.eq(true),
                Mockito.eq(true),
                Mockito.eq(true),
                Mockito.eq(false),
                Mockito.eq(false));
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
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(null);
        var schedulingInfoService = createSchedulingInfoService();

        schedulingInfoService.reserveSchedulingInfo(VmrType.lecture,
                ViewType.one_main_zero_pips,
                ViewType.four_mains_zero_pips,
                VmrQuality.full_hd,
                true,
                true,
                true,
                false,
                false);
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
        schedulingTemplate.setVmrType(VmrType.conference);
        schedulingTemplate.setHostView(ViewType.one_main_seven_pips);
        schedulingTemplate.setGuestView(ViewType.two_mains_twentyone_pips);
        schedulingTemplate.setVmrQuality(VmrQuality.full_hd);
        schedulingTemplate.setEnableOverlayText(true);
        schedulingTemplate.setGuestsCanPresent(true);
        schedulingTemplate.setForcePresenterIntoMain(true);
        schedulingTemplate.setForceEncryption(false);
        schedulingTemplate.setMuteAllGuests(false);
        schedulingTemplate.setCustomPortalHost("some_portal_host");
        schedulingTemplate.setCustomPortalHost("some_portal_host");
        schedulingTemplate.setReturnUrl("some_return_url");

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

        return schedulingInfo;
    }

    private SchedulingInfoServiceImpl createSchedulingInfoService() {
        return createSchedulingInfoService(new CustomUriValidatorImpl());
    }

    private SchedulingInfoServiceImpl createSchedulingInfoService(CustomUriValidator customUriValidator) {
        return new SchedulingInfoServiceImpl(schedulingInfoRepository, schedulingTemplateRepository, schedulingTemplateService, null, meetingUserService, organizationRepository, organisationStrategy, userContextService, "overflow", organisationTreeServiceClient, auditService, customUriValidator);
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
