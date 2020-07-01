package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dao.SchedulingTemplate;
import dk.medcom.video.api.dto.PoolInfoDto;
import dk.medcom.video.api.dto.ProvisionStatus;
import dk.medcom.video.api.organisation.OrganisationStrategy;
import dk.medcom.video.api.repository.OrganisationRepository;
import dk.medcom.video.api.repository.PoolInfoRepository;
import dk.medcom.video.api.repository.SchedulingInfoRepository;
import dk.medcom.video.api.repository.SchedulingTemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class PoolInfoServiceTest {

	@Mock
    PoolInfoRepository poolInfoRepository;

	
    @Before
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGetPoolInfo() {
        SchedulingInfoRepository schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        OrganisationRepository organisationRepository = Mockito.mock(OrganisationRepository.class);
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);
        Mockito.when(organisationRepository.findByOrganisationId(Mockito.anyString())).thenReturn(new Organisation());

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.singletonList(schedulingTemplate));

        OrganisationStrategy organisationStrategy = Mockito.mock(OrganisationStrategy.class);
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());

        
        
		PoolInfoService poolInfoService = new PoolInfoService(organisationRepository, schedulingInfoRepository, schedulingTemplateRepository, organisationStrategy, poolInfoRepository);

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.get(0);
        assertEquals(organisations.get(0).getOrganisationId(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.get(0).getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
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
        SchedulingInfoRepository schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndProvisionStatus(ProvisionStatus.PROVISIONED_OK)).thenReturn(createSchedulingInfo());

        List<Organisation> organisations = createOrganisationList();
        OrganisationRepository organisationRepository = Mockito.mock(OrganisationRepository.class);
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(organisations);

        SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        Mockito.when(schedulingTemplateRepository.findByOrganisationAndIsDefaultTemplateAndDeletedTimeIsNull(Mockito.any(), Mockito.anyBoolean())).thenReturn(Collections.emptyList());

        OrganisationStrategy organisationStrategy = Mockito.mock(OrganisationStrategy.class);
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(createStrategyOrganisationList());

        PoolInfoService poolInfoService = new PoolInfoService(organisationRepository, schedulingInfoRepository, schedulingTemplateRepository, organisationStrategy, poolInfoRepository);

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(2, response.size());

        PoolInfoDto firstPoolInfo = response.get(0);
        assertEquals(organisations.get(0).getOrganisationId(), firstPoolInfo.getOrganizationId());
        assertEquals(organisations.get(0).getPoolSize().intValue(), firstPoolInfo.getDesiredPoolSize());
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
        OrganisationRepository organisationRepository = Mockito.mock(OrganisationRepository.class);
        Mockito.when(organisationRepository.findByPoolSizeNotNull()).thenReturn(Collections.emptyList());

        SchedulingInfoRepository schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
//        Mockito.when(schedulingInfoRepository.findByMeetingIsNull()).thenReturn(Collections.emptyList());

        SchedulingTemplate schedulingTemplate = createDefaultSchedulingTemplate();
        SchedulingTemplateRepository schedulingTemplateRepository = Mockito.mock(SchedulingTemplateRepository.class);
        Mockito.when(schedulingTemplateRepository.findByOrganisationIsNullAndDeletedTimeIsNull()).thenReturn(Collections.singletonList(schedulingTemplate));

        OrganisationStrategy organisationStrategy = Mockito.mock(OrganisationStrategy.class);
        Mockito.when(organisationStrategy.findByPoolSizeNotNull()).thenReturn(Collections.emptyList());

        PoolInfoService poolInfoService = new PoolInfoService(organisationRepository, schedulingInfoRepository, schedulingTemplateRepository, organisationStrategy, poolInfoRepository);

        List<PoolInfoDto> response = poolInfoService.getPoolInfo();

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    private List<SchedulingInfo> createSchedulingInfo() {
        List<SchedulingInfo> schedulingInfos = new ArrayList<>();

        SchedulingInfo sched1 = new SchedulingInfo();
        sched1.setOrganisation(createOrganiztion(1));
        schedulingInfos.add(sched1);

        SchedulingInfo sched2 = new SchedulingInfo();
        sched2.setOrganisation(createOrganiztion(1));
        schedulingInfos.add(sched2);

        return schedulingInfos;
    }

    private List<Organisation> createOrganisationList() {
        List<Organisation> organisations = new ArrayList<>();

        organisations.add(createOrganiztion(1));
        organisations.add(createOrganiztion(2));

        return organisations;
    }

    private List<dk.medcom.video.api.organisation.Organisation> createStrategyOrganisationList() {
        List<Organisation> organisations = new ArrayList<>();

        organisations.add(createOrganiztion(1));
        organisations.add(createOrganiztion(2));

        List<dk.medcom.video.api.organisation.Organisation> returnOrganisations = new ArrayList<>();
        organisations.forEach(x -> {
            dk.medcom.video.api.organisation.Organisation organisation = new dk.medcom.video.api.organisation.Organisation();
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
}
