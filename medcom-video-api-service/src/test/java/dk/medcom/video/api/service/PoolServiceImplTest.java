package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.api.SchedulingTemplateDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

public class PoolServiceImplTest {
    private PoolServiceImpl poolService;
    private PoolInfoService poolInfoService;
    private SchedulingInfoService schedulingInfoService;
    private MeetingUserRepository meetingUserRepository;
    private OrganisationRepository organisationRepository;
    private NewProvisionerOrganisationFilter newProvisionerOrganisationFilter;

    private final String poolOrganisation = "POOL_ORG";
    private final String poolEmail = "POOL_EMAIL";

    @Before
    public void setup() {
        poolInfoService = Mockito.mock(PoolInfoService.class);
        schedulingInfoService = Mockito.mock(SchedulingInfoService.class);
        meetingUserRepository = Mockito.mock(MeetingUserRepository.class);
        organisationRepository = Mockito.mock(OrganisationRepository.class);
        newProvisionerOrganisationFilter = Mockito.mock(NewProvisionerOrganisationFilter.class);

        poolService = new PoolServiceImpl(poolInfoService, schedulingInfoService, meetingUserRepository, organisationRepository, newProvisionerOrganisationFilter, poolOrganisation, poolEmail);
    }

    @Test
    public void testFillPool() throws NotValidDataException, NotAcceptableException, PermissionDeniedException {
        var noFillPoll = new PoolInfoDto();
        noFillPoll.setDesiredPoolSize(10);
        noFillPoll.setOrganizationId("org1");
        noFillPoll.setSchedulingTemplate(new SchedulingTemplateDto());
        noFillPoll.setAvailablePoolSize(10);

        var fillPool = new PoolInfoDto();
        fillPool.setDesiredPoolSize(20);
        fillPool.setOrganizationId("org2");
        fillPool.setSchedulingTemplate(new SchedulingTemplateDto());
        fillPool.setAvailablePoolSize(19);

        var notMigratedPool = new PoolInfoDto();
        notMigratedPool.setDesiredPoolSize(10);
        notMigratedPool.setOrganizationId("not_migrated");
        notMigratedPool.setSchedulingTemplate(new SchedulingTemplateDto());
        notMigratedPool.setAvailablePoolSize(5);

        var meetingUser = new MeetingUser();

        Mockito.when(poolInfoService.getPoolInfo()).thenReturn(Arrays.asList(noFillPoll, fillPool, notMigratedPool));

        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(true);
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org2")).thenReturn(true);

        var organisation = new Organisation();
        Mockito.when(organisationRepository.findByOrganisationId(poolOrganisation)).thenReturn(organisation);

        Mockito.when(meetingUserRepository.findOneByOrganisationAndEmail(organisation, poolEmail)).thenReturn(meetingUser);

        poolService.fillPools();

        Mockito.verify(poolInfoService, times(1)).getPoolInfo();
        Mockito.verify(newProvisionerOrganisationFilter, times(2)).newProvisioner("org1");
        Mockito.verify(newProvisionerOrganisationFilter, times(2)).newProvisioner("org2");
        Mockito.verify(newProvisionerOrganisationFilter, times(2)).newProvisioner("not_migrated");
        Mockito.verify(organisationRepository, times(1)).findByOrganisationId(poolOrganisation);
        Mockito.verify(meetingUserRepository, times(1)).findOneByOrganisationAndEmail(organisation, poolEmail);
        Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfoWithCustomCreatedBy(Mockito.argThat(x -> {
            assertEquals(fillPool.getOrganizationId(), x.getOrganizationId());

            return true;
        }), Mockito.eq(meetingUser));

        Mockito.verifyNoMoreInteractions(poolInfoService, newProvisionerOrganisationFilter, organisationRepository, meetingUserRepository, schedulingInfoService);
    }
}
