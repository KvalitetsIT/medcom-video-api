package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.CreateMeetingDto;
import dk.medcom.video.api.dao.entity.ProvisionStatus;
import dk.medcom.video.api.dao.entity.VmrQuality;
import dk.medcom.video.api.dao.SchedulingInfoRepository;
import dk.medcom.video.api.organisation.model.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.PoolFinderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PoolFinderServiceTest {
    private PoolFinderServiceImpl poolFinderService;
    private SchedulingInfoRepository schedulingInfoRepository;

    @BeforeEach
    public void setup() {
        schedulingInfoRepository = Mockito.mock(SchedulingInfoRepository.class);
        int minimumAgeSeconds = 120;
        poolFinderService = new PoolFinderServiceImpl(schedulingInfoRepository, minimumAgeSeconds);
    }

    @Test
    public void testFindPoolSchedulingInfoDefault() {
        var orgCode = "123";

        var organisation = new Organisation();
        organisation.setCode(orgCode);

        var schedulingInfo = new SchedulingInfo();

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgCode),
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
                Mockito.eq(orgCode),
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
        var orgCode = "123";

        var organisation = new Organisation();
        organisation.setCode(orgCode);

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgCode),
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
                Mockito.eq(orgCode),
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
        var orgCode = "123";

        var organisation = new Organisation();
        organisation.setCode(orgCode);

        var createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setVmrQuality(VmrQuality.fullhd);

        var schedulingInfo = new SchedulingInfo();
        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgCode),
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
                Mockito.eq(orgCode),
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
        var orgCode = "123";

        var organisation = new Organisation();
        organisation.setCode(orgCode);

        var createMeetingDto = new CreateMeetingDto();
        createMeetingDto.setVmrQuality(VmrQuality.fullhd);

        Mockito.when(schedulingInfoRepository.findByMeetingIsNullAndOrganisationAndProvisionStatus(
                Mockito.eq(orgCode),
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
                Mockito.eq(orgCode),
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
