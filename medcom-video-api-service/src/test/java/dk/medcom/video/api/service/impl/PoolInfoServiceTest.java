package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.SchedulingTemplateRepository;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.dao.entity.SchedulingTemplate;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.organisation.model.Organisation;
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
    private SchedulingInfoRepository schedulingInfoRepository;
    private SchedulingTemplateRepository schedulingTemplateRepository;
    private OrganisationStrategy organisationStrategy;

    private PoolInfoService poolInfoService;

    @BeforeEach
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        organisationStrategy = Mockito.mock(OrganisationStrategy.class);

        poolInfoService = new PoolInfoServiceImpl(schedulingInfoRepository, schedulingTemplateRepository, organisationStrategy);
    }

    @Test
    public void testGetPoolInfoWithPoolTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(organisations);

        SchedulingTemplate schedulingTemplate = createPoolSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationCodeAndIsPoolTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.singletonList(schedulingTemplate));

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getCode(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNotNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getCode(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNotNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoPoolTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(organisations);

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationCodeAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.singletonList(schedulingTemplate));

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getCode(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNotNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getCode(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNotNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoDefaultTemplate() {
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndReservationIdIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(organisations);

        Mockito.when(schedulingTemplateRepository.findByOrganisationCodeAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.emptyList());

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.getFirst();
        assertEquals(organisations.getFirst().getCode(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.getFirst().getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
        assertEquals(2, firstPoolInfo.getAvailablePoolSize());

        assertNull(firstPoolInfo.getSchedulingTemplate());

        PoolInfoDto secondPoolInfo = response.get(1);
        assertEquals(organisations.get(1).getCode(), secondPoolInfo.getOrganizationId());
        assertEquals(organisations.get(1).getPoolSize().intValue(), secondPoolInfo.getDesiredPoolSize());
        assertEquals(0, secondPoolInfo.getAvailablePoolSize());
        assertNull(secondPoolInfo.getSchedulingTemplate());
    }

    @Test
    public void testGetPoolInfoNoConfiguredPools() {
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(Collections.emptyList());

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        Mockito.when(schedulingTemplateRepository.findByOrganisationCodeIsNullAndDeletedTimeIsNull()).thenReturn(Collections.singletonList(schedulingTemplate));

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
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(organisations);

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
        sched1.setOrganisationCode(createOrganiztion(1).getCode());
        sched1.setCreatedTime(new Date());
        schedulingInfos.add(sched1);

        SchedulingInfo sched2 = new SchedulingInfo();
        sched2.setOrganisationCode(createOrganiztion(1).getCode());
        sched2.setCreatedTime(new Date());
        schedulingInfos.add(sched2);

        return schedulingInfos;
    }

    private List<SchedulingInfo> createSchedulingInfoListWithCreatedTime(Date date1, Date date2, Date date3) {
        List<SchedulingInfo> schedulingInfoList = new ArrayList<>();

        String organisationCode1 = createOrganiztion(1).getCode();
        String organisationCode2 = createOrganiztion(2).getCode();

        SchedulingInfo schedulingInfo1 = new SchedulingInfo();
        schedulingInfo1.setCreatedTime(date1);
        schedulingInfo1.setOrganisationCode(organisationCode1);

        SchedulingInfo schedulingInfo2 = new SchedulingInfo();
        schedulingInfo2.setCreatedTime(date2);
        schedulingInfo2.setOrganisationCode(organisationCode1);

        SchedulingInfo schedulingInfo3 = new SchedulingInfo();
        schedulingInfo3.setCreatedTime(date3);
        schedulingInfo3.setOrganisationCode(organisationCode1);

        SchedulingInfo schedulingInfo4 = new SchedulingInfo();
        schedulingInfo4.setCreatedTime(date3);
        schedulingInfo4.setOrganisationCode(organisationCode2);

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

    private Organisation createOrganiztion(int organizationId) {
        Organisation organization = new Organisation();
        organization.setPoolSize(10 + organizationId);
        organization.setCode("test-org " + organizationId);
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
