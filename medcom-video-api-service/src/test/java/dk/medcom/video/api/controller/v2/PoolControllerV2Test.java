package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.service.NewProvisionerOrganisationFilter;
import dk.medcom.video.api.service.PoolInfoServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static dk.medcom.video.api.controller.v2.HelperMethods.*;
import static org.junit.jupiter.api.Assertions.*;

public class PoolControllerV2Test {

    private PoolControllerV2 poolControllerV2;
    private PoolInfoServiceV2 poolInfoService;
    private NewProvisionerOrganisationFilter provisionerOrganisationFilter;

    @BeforeEach
    public void setup() {
        poolInfoService = Mockito.mock(PoolInfoServiceV2.class);
        provisionerOrganisationFilter = Mockito.mock(NewProvisionerOrganisationFilter.class);

        poolControllerV2 = new PoolControllerV2(poolInfoService, provisionerOrganisationFilter);
    }

    private void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(poolInfoService, provisionerOrganisationFilter);
    }

    @Test
    public void testV2PoolGet() {
        var org1 = randomString();
        var org2 = randomString();

        Mockito.when(provisionerOrganisationFilter.newProvisioner(org1)).thenReturn(true);
        Mockito.when(provisionerOrganisationFilter.newProvisioner(org2)).thenReturn(false);

        var poolInfos = List.of(randomPoolInfoInOrganisation(org1), randomPoolInfoInOrganisation(org2));

        Mockito.when(poolInfoService.getPoolInfoV2()).thenReturn(poolInfos);

        var result = poolControllerV2.v2PoolGet();
        assertNotNull(result.getStatusCode());
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());

        assertPoolInfo(poolInfos.getLast(), result.getBody().getFirst());

        Mockito.verify(poolInfoService).getPoolInfoV2();
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org1);
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org2);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2PoolGetNoFilteredOut() {
        var org1 = randomString();
        var org2 = randomString();

        Mockito.when(provisionerOrganisationFilter.newProvisioner(Mockito.any())).thenReturn(false);

        var poolInfos = List.of(randomPoolInfoInOrganisation(org1), randomPoolInfoInOrganisation(org2));

        Mockito.when(poolInfoService.getPoolInfoV2()).thenReturn(poolInfos);

        var result = poolControllerV2.v2PoolGet();
        assertNotNull(result.getStatusCode());
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());

        assertPoolInfo(poolInfos.getFirst(), result.getBody().getFirst());
        assertPoolInfo(poolInfos.getLast(), result.getBody().getLast());

        Mockito.verify(poolInfoService).getPoolInfoV2();
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org1);
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org2);
        verifyNoMoreInteractions();
    }

    @Test
    public void testV2PoolGetAllFilteredOut() {
        var org1 = randomString();
        var org2 = randomString();

        Mockito.when(provisionerOrganisationFilter.newProvisioner(Mockito.any())).thenReturn(true);

        var poolInfos = List.of(randomPoolInfoInOrganisation(org1), randomPoolInfoInOrganisation(org2));

        Mockito.when(poolInfoService.getPoolInfoV2()).thenReturn(poolInfos);

        var result = poolControllerV2.v2PoolGet();
        assertNotNull(result.getStatusCode());
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertTrue(result.getBody().isEmpty());

        Mockito.verify(poolInfoService).getPoolInfoV2();
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org1);
        Mockito.verify(provisionerOrganisationFilter).newProvisioner(org2);
        verifyNoMoreInteractions();
    }
}
