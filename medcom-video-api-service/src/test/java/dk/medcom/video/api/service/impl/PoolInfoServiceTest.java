package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.service.PoolInfoService;
import dk.medcom.video.api.service.PoolInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PoolInfoServiceTest {
	@Mock
    private PoolInfoRepository poolInfoRepository;

    private OrganisationRepository organisationRepository;
    private SchedulingInfoRepository schedulingInfoRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;
    private OrganisationStrategy organisationStrategy;

    private PoolInfoService poolInfoService;

    @BeforeEach
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        organisationRepository = Mockito.mock(OrganisationRepository.class);
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        organisationStrategy = Mockito.mock(OrganisationStrategy.class);

        poolInfoService = new PoolInfoServiceImpl(organisationRepository, schedulingInfoRepository, schedulingTemplateRepository, organisationStrategy, poolInfoRepository);
    }

    @Test
    public void testGetPoolInfoWithPoolTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);
        Mockito.when(organisationRepository.findByOrganisationId(Mockito.anyString())).thenReturn(new Organisation());

        SchedulingTemplate schedulingTemplate = createPoolSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsPoolTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.singletonList(schedulingTemplate));

        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());


        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getOrganisationId(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNotNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getOrganisationId(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNotNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoPoolTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);
        Mockito.when(organisationRepository.findByOrganisationId(Mockito.anyString())).thenReturn(new Organisation());

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.singletonList(schedulingTemplate));

        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());


        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getOrganisationId(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNotNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getOrganisationId(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNotNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoDefaultTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);

        Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.emptyList());

        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());


        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getOrganisationId(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getOrganisationId(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoConfiguredPools() {
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(Collections.emptyList());

//        Mockito.when(schedulingInfoRepository.findByMeetingIsNull()).thenReturn(Collections.emptyList());

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationIsNullAndDeletedTimeIsNull()).thenReturn(Collections.singletonList(schedulingTemplate));

        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(Collections.emptyList());


        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void testGetPoolInfoSortsSchedulingInfoAfterCreatedTimeDescending() {
        Date yesterday = new Date(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli());
        Date now = new Date(Instant.now().toEpochMilli());
        Date someTimeAgo = new Date(Instant.now().minus(45, ChronoUnit.MINUTES).toEpochMilli());

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfoListWithCreatedTime(yesterday, now, someTimeAgo));

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);

        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());

        var result = poolInfoService.getPoolInfo();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertNotNull(result.getFirst());
        var org1 = result.getFirst();
        assertEquals(3, org1.getSchedulingInfoList().size());
        assertEquals(now, org1.getSchedulingInfoList().get(0).getCreatedTime());
        assertEquals(someTimeAgo, org1.getSchedulingInfoList().get(1).getCreatedTime());
        assertEquals(yesterday, org1.getSchedulingInfoList().get(2).getCreatedTime());

        assertNotNull(result.get(1));
        var org2 = result.get(1);
        assertEquals(1, org2.getSchedulingInfoList().size());
        assertEquals(someTimeAgo, org2.getSchedulingInfoList().getFirst().getCreatedTime());
    }

    private List<SchedulingInfo> createSchedulingInfo() {
        List<SchedulingInfo> schedulingInfos = new ArrayList<>();

        SchedulingInfo sched1 = new SchedulingInfo();
        sched1.setOrganisation(createOrganiztion(1));
        sched1.setCreatedTime(new Date());
        schedulingInfos.add(sched1);

        SchedulingInfo sched2 = new SchedulingInfo();
        sched2.setOrganisation(createOrganiztion(1));
        sched2.setCreatedTime(new Date());
        schedulingInfos.add(sched2);

        return schedulingInfos;
    }

    private List<SchedulingInfo> createSchedulingInfoListWithCreatedTime(Date date1, Date date2, Date date3) {
        List<SchedulingInfo> schedulingInfoList = new ArrayList<>();

        Organisation organisation1 = createOrganiztion(1);
        Organisation organisation2 = createOrganiztion(2);

        SchedulingInfo schedulingInfo1 = new SchedulingInfo();
        schedulingInfo1.setCreatedTime(date1);
        schedulingInfo1.setOrganisation(organisation1);

        SchedulingInfo schedulingInfo2 = new SchedulingInfo();
        schedulingInfo2.setCreatedTime(date2);
        schedulingInfo2.setOrganisation(organisation1);

        SchedulingInfo schedulingInfo3 = new SchedulingInfo();
        schedulingInfo3.setCreatedTime(date3);
        schedulingInfo3.setOrganisation(organisation1);

        SchedulingInfo schedulingInfo4 = new SchedulingInfo();
        schedulingInfo4.setCreatedTime(date3);
        schedulingInfo4.setOrganisation(organisation2);

        schedulingInfoList.add(schedulingInfo1);
        schedulingInfoList.add(schedulingInfo2);
        schedulingInfoList.add(schedulingInfo3);
        schedulingInfoList.add(schedulingInfo4);

        return schedulingInfoList;
    }

    private List<Organisation> createOrganisationList() {
        List<Organisation> organisations = new ArrayList<>();

        organisations.add(createOrganiztion(1));
        organisations.add(createOrganiztion(2));

        return organisations;
    }

    private List<dk.medcom.video.api.organisation.model.Organisation> createStrategyOrganisationList() {
        List<Organisation> organisations = new ArrayList<>();

        organisations.add(createOrganiztion(1));
        organisations.add(createOrganiztion(2));

        List<dk.medcom.video.api.organisation.model.Organisation> returnOrganisations = new ArrayList<>();
        organisations.forEach(x -> {
            dk.medcom.video.api.organisation.model.Organisation organisation = new dk.medcom.video.api.organisation.model.Organisation();
            organisation.setCode(x.getOrganisationId());
            organisation.setPoolSize(x.getPoolSize());

            returnOrganisations.add(organisation);
        });

        return returnOrganisations;
    }

    private Organisation createOrganiztion(int organizationId) {
        Organisation organization = new Organisation();
        organization.setId((long) organizationId);
        organization.setPoolSize(10 + organizationId);
        organization.setOrganisationId("test-org " + organizationId);
        organization.setName("This is a name " + organizationId);

        return organization;
    }

    private SchedulingTemplate createDefaultSchedulingTemplate() {
        SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
        schedulingTemplate.setIsDefaultTemplate(true);
        schedulingTemplate.setConferencingSysId(1L);
        schedulingTemplate.setEndMeetingOnEndTime(true);
        schedulingTemplate.setGuestPinRangeHigh(2000L);
        schedulingTemplate.setGuestPinRangeLow(1000L);
        schedulingTemplate.setGuestPinRequired(true);
        schedulingTemplate.setHostPinRangeHigh(4000L);
        schedulingTemplate.setHostPinRangeLow(3000L);
        schedulingTemplate.setHostPinRequired(false);
        schedulingTemplate.setIvrTheme("some theme");
        schedulingTemplate.setMaxParticipants(10);
        schedulingTemplate.setUriDomain("uri domain");
        schedulingTemplate.setUriNumberRangeHigh(9000L);
        schedulingTemplate.setUriNumberRangeLow(8000L);
        schedulingTemplate.setUriPrefix("uri prefix");
        schedulingTemplate.setVMRAvailableBefore(10);
        schedulingTemplate.setId(2L);

        return schedulingTemplate;
    }

    private SchedulingTemplate createPoolSchedulingTemplate() {
        SchedulingTemplate schedulingTemplate = new SchedulingTemplate();
        schedulingTemplate.setIsPoolTemplate(true);
        schedulingTemplate.setConferencingSysId(1L);
        schedulingTemplate.setEndMeetingOnEndTime(true);
        schedulingTemplate.setGuestPinRangeHigh(2000L);
        schedulingTemplate.setGuestPinRangeLow(1000L);
        schedulingTemplate.setGuestPinRequired(true);
        schedulingTemplate.setHostPinRangeHigh(4000L);
        schedulingTemplate.setHostPinRangeLow(3000L);
        schedulingTemplate.setHostPinRequired(false);
        schedulingTemplate.setIvrTheme("some theme");
        schedulingTemplate.setMaxParticipants(10);
        schedulingTemplate.setUriDomain("uri domain");
        schedulingTemplate.setUriNumberRangeHigh(9000L);
        schedulingTemplate.setUriNumberRangeLow(8000L);
        schedulingTemplate.setUriPrefix("uri prefix");
        schedulingTemplate.setVMRAvailableBefore(10);
        schedulingTemplate.setId(2L);

        return schedulingTemplate;
    }
}
