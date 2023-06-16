package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.api.ProvisionStatus;
import dk.medcom.video.api.api.VmrQuality;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PoolFinderServiceTest {
    private PoolFinderServiceImpl poolFinderService;
    private int minimumAgeSeconds;
    private SchedulingInfoRepository schedulingInfoRepository;

    @Before
    public void setup() {
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        minimumAgeSeconds = 120;
        poolFinderService = new PoolFinderServiceImpl(schedulingInfoRepository, minimumAgeSeconds);
    }

    @Test
    public void testFindPoolSchedulingInfoDefault() {
        var orgId = 123L;

        var organisation = new Organisation();
        organisation.setId(orgId);

        var schedulingInfo = new SchedulingInfo();

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
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
                Mockito.any())).thenReturn(Collections.singletonList(schedulingInfo));

        var result = poolFinderService.findPoolSubject(organisation, null);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(schedulingInfo, result.get());

        Mockito.verify(schedulingInfoRepository).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
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

        Mockito.verifyNoMoreInteractions(schedulingInfoRepository);
    }

    @Test
    public void testFindPoolSchedulingInfoNotFound() {
        var orgId = 123L;

        var organisation = new Organisation();
        organisation.setId(orgId);

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
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
                Mockito.any())).thenReturn(Collections.emptyList());

        var result = poolFinderService.findPoolSubject(organisation, null);
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(schedulingInfoRepository).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
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

        Mockito.verifyNoMoreInteractions(schedulingInfoRepository);
    }

    @Test
    public void testFindPoolSchedulingInfoWithFieldsFromCreateMeeting() {
        var orgId = 123L;

        var organisation = new Organisation();
        organisation.setId(orgId);

        var createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setVmrQuality(VmrQuality.fullhd);

        var schedulingInfo = new SchedulingInfo();
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(VmrQuality.fullhd.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(Collections.singletonList(schedulingInfo));

        var result = poolFinderService.findPoolSubject(organisation, createMeetingDto);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(schedulingInfo, result.get());

        Mockito.verify(schedulingInfoRepository).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(VmrQuality.fullhd.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any());

        Mockito.verifyNoMoreInteractions(schedulingInfoRepository);
    }

    @Test
    public void testFindPoolSchedulingInfoWithFieldsFromCreateMeetingNotFound() {
        var orgId = 123L;

        var organisation = new Organisation();
        organisation.setId(orgId);

        var createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setVmrQuality(VmrQuality.fullhd);

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(VmrQuality.fullhd.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any())).thenReturn(Collections.emptyList());

        var result = poolFinderService.findPoolSubject(organisation, createMeetingDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(schedulingInfoRepository).findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgId),
                Mockito.eq(ProvisionStatus.PROVISIONED_OK.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.eq(VmrQuality.fullhd.name()),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any());

        Mockito.verifyNoMoreInteractions(schedulingInfoRepository);
    }
}
