package dk.medcom.video.api.service.impl.v2;

import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.service.PoolInfoService;
import dk.medcom.video.api.service.PoolInfoServiceV2;
import dk.medcom.video.api.service.PoolInfoServiceV2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static dk.medcom.video.api.service.impl.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class PoolInfoServiceV2ImplTest {
    private PoolInfoServiceV2 poolInfoServiceV2;

    private PoolInfoService poolInfoService;
    private final String shortLinkBaseUrl = "base.url";

    @BeforeEach
    public void setup() {
        poolInfoService = Mockito.mock(PoolInfoService.class);

        poolInfoServiceV2 = new PoolInfoServiceV2Impl(poolInfoService, shortLinkBaseUrl);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(poolInfoService);
    }

    @Test
    public void testGetPoolInfoV2() throws PermissionDeniedException {
        var poolInfos = List.of(randomPoolInfo(), randomPoolInfo());
        Mockito.when(poolInfoService.getPoolInfo()).thenReturn(poolInfos);

        var result = poolInfoServiceV2.getPoolInfoV2();
        assertNotNull(result);
        assertEquals(2, result.size());

        var res1 = result.stream().filter(x -> x.organisationId().equals(poolInfos.getFirst().getOrganizationId())).findFirst().orElseThrow();
        var res2 = result.stream().filter(x -> x.organisationId().equals(poolInfos.getLast().getOrganizationId())).findFirst().orElseThrow();

        assertPoolInfo(poolInfos.getFirst(), shortLinkBaseUrl, res1);
        assertPoolInfo(poolInfos.getLast(), shortLinkBaseUrl, res2);

        Mockito.verify(poolInfoService).getPoolInfo();
        verifyNoMoreInteractions();
    }

    @Test
    public void testGetPoolInfoV2NoPoolInfo() {
        Mockito.when(poolInfoService.getPoolInfo()).thenReturn(List.of());

        var result = poolInfoServiceV2.getPoolInfoV2();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        Mockito.verify(poolInfoService).getPoolInfo();
        verifyNoMoreInteractions();
    }
}
