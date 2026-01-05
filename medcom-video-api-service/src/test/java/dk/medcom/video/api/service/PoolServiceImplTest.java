package dk.medcom.video.api.service;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.api.SchedulingTemplateDto;
import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.RessourceNotFoundException;
import dk.medcom.video.api.dao.MeetingUserRepository;
import dk.medcom.video.api.dao.OrganisationRepository;
import dk.medcom.video.api.dao.entity.MeetingUser;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

public class PoolServiceImplTest {
    private PoolServiceImpl poolService;
    private SchedulingInfoService schedulingInfoService;
    private MeetingUserRepository meetingUserRepository;
    private OrganisationRepository organisationRepository;
    private NewProvisionerOrganisationFilter newProvisionerOrganisationFilter;

    private final String poolOrganisation = "POOL_ORG";
    private final String poolEmail = "POOL_EMAIL";

    @Before
    public void setup() {
        schedulingInfoService = Mockito.mock(SchedulingInfoService.class);
        meetingUserRepository = Mockito.mock(MeetingUserRepository.class);
        organisationRepository = Mockito.mock(OrganisationRepository.class);
        newProvisionerOrganisationFilter = Mockito.mock(NewProvisionerOrganisationFilter.class);

        poolService = new PoolServiceImpl(schedulingInfoService, meetingUserRepository, organisationRepository, newProvisionerOrganisationFilter, poolOrganisation, poolEmail);
    }

    @Test
    public void testFillOrDeletePool_WhenDesiredPoolSizeEqualsAvailablePoolSize_DoesNothing() {
        var poolInfo = new PoolInfoDto();
        poolInfo.setDesiredPoolSize(10);
        poolInfo.setOrganizationId("org1");
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto());
        poolInfo.setAvailablePoolSize(10);
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(true);

        poolService.fillOrDeletePool(poolInfo);

        Mockito.verifyNoInteractions(schedulingInfoService, meetingUserRepository);
    }

    @Test
    public void testFillOrDeletePool_WhenNewProvisionerReturnsFalse_DoesNothing() {
        var poolInfo = new PoolInfoDto();
        poolInfo.setDesiredPoolSize(10);
        poolInfo.setOrganizationId("org1");
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto());
        poolInfo.setAvailablePoolSize(20);
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(false);

        poolService.fillOrDeletePool(poolInfo);

        Mockito.verifyNoInteractions(schedulingInfoService, meetingUserRepository);
    }

    @Test
    public void testFillOrDeletePool_WhenNewProvisionerReturnsTrueAndDesiredIsGreaterThanAvailable_FillsPool() throws NotValidDataException, NotAcceptableException {
        var poolInfo = new PoolInfoDto();
        poolInfo.setDesiredPoolSize(20);
        poolInfo.setOrganizationId("org1");
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto());
        poolInfo.setAvailablePoolSize(10);
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(true);
        var organisation = new Organisation();
        Mockito.when(organisationRepository.findByOrganisationId(poolOrganisation)).thenReturn(organisation);
        var meetingUser = new MeetingUser();
        Mockito.when(meetingUserRepository.findOneByOrganisationAndEmail(organisation, poolEmail)).thenReturn(meetingUser);

        poolService.fillOrDeletePool(poolInfo);

        Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfoWithCustomCreatedBy(
                Mockito.argThat(x -> poolInfo.getOrganizationId().equals(x.getOrganizationId())),
                Mockito.eq(meetingUser));
        Mockito.verifyNoMoreInteractions(schedulingInfoService);
    }

    @Test
    public void testFillOrDeletePool_WhenFillingAndNoExistingMeetingUser_SavesNewUserAndFillsPool() throws NotValidDataException, NotAcceptableException {
        var poolInfo = new PoolInfoDto();
        poolInfo.setDesiredPoolSize(20);
        poolInfo.setOrganizationId("org1");
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto());
        poolInfo.setAvailablePoolSize(10);
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(true);
        var organisation = new Organisation();
        Mockito.when(organisationRepository.findByOrganisationId(poolOrganisation)).thenReturn(organisation);
        Mockito.when(meetingUserRepository.findOneByOrganisationAndEmail(organisation, poolEmail)).thenReturn(null);
        var savedMeetingUser = Mockito.mock(MeetingUser.class);
        Mockito.when(meetingUserRepository.save(Mockito.argThat(user -> poolEmail.equals(user.getEmail()) && organisation.equals(user.getOrganisation()))))
                .thenReturn(savedMeetingUser);

        poolService.fillOrDeletePool(poolInfo);

        Mockito.verify(schedulingInfoService, times(1)).createSchedulingInfoWithCustomCreatedBy(
                Mockito.argThat(info -> poolInfo.getOrganizationId().equals(info.getOrganizationId())),
                Mockito.eq(savedMeetingUser)
        );
        Mockito.verifyNoMoreInteractions(schedulingInfoService);
    }

    @Test
    public void testFillOrDeletePool_WhenNewProvisionerReturnsTrueAndDesiredIsLessThanAvailable_DeletesFromPool() throws RessourceNotFoundException {
        var poolInfo = new PoolInfoDto();
        poolInfo.setDesiredPoolSize(2);
        poolInfo.setOrganizationId("org1");
        poolInfo.setSchedulingTemplate(new SchedulingTemplateDto());
        poolInfo.setAvailablePoolSize(4);
        poolInfo.setSchedulingInfoList(createSchedulingInfoListWithCreatedTime("uuid1", "uuid2", "uuid3", "uuid4"));
        Mockito.when(newProvisionerOrganisationFilter.newProvisioner("org1")).thenReturn(true);

        poolService.fillOrDeletePool(poolInfo);

        Mockito.verify(schedulingInfoService, times(0)).deleteSchedulingInfoPool("uuid1");
        Mockito.verify(schedulingInfoService, times(0)).deleteSchedulingInfoPool("uuid2");
        Mockito.verify(schedulingInfoService, times(1)).deleteSchedulingInfoPool("uuid3");
        Mockito.verify(schedulingInfoService, times(1)).deleteSchedulingInfoPool("uuid4");
        Mockito.verifyNoMoreInteractions(schedulingInfoService);
    }

    private List<SchedulingInfo> createSchedulingInfoListWithCreatedTime(String uuid1, String uuid2, String uuid3, String uuid4) {
        List<SchedulingInfo> schedulingInfoList = new ArrayList<>();

        SchedulingInfo schedulingInfo1 = new SchedulingInfo();
        schedulingInfo1.setUuid(uuid1);

        SchedulingInfo schedulingInfo2 = new SchedulingInfo();
        schedulingInfo2.setUuid(uuid2);

        SchedulingInfo schedulingInfo3 = new SchedulingInfo();
        schedulingInfo3.setUuid(uuid3);

        SchedulingInfo schedulingInfo4 = new SchedulingInfo();
        schedulingInfo4.setUuid(uuid4);

        schedulingInfoList.add(schedulingInfo1);
        schedulingInfoList.add(schedulingInfo2);
        schedulingInfoList.add(schedulingInfo3);
        schedulingInfoList.add(schedulingInfo4);

        return schedulingInfoList;
    }
}
