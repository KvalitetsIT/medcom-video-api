package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.api.PoolInfoDto;
import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.entity.PoolHistory;
import dk.medcom.video.api.service.PoolHistoryServiceImpl;
import dk.medcom.video.api.service.PoolInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

public class PoolHistoryServiceImplTest {
    private PoolHistoryServiceImpl poolHistoryServiceImpl;
    private PoolInfoService poolInfoService;
    private PoolHistoryDao poolHistoryDao;

    @BeforeEach
    public void setup() {
        poolInfoService = Mockito.mock(PoolInfoService.class);
        poolHistoryDao = Mockito.mock(PoolHistoryDao.class);

        poolHistoryServiceImpl = new PoolHistoryServiceImpl(poolInfoService, poolHistoryDao);
    }

    @Test
    public void testNoPools() {
        Mockito.when(poolInfoService.getPoolInfo()).thenReturn(Collections.emptyList());

        poolHistoryServiceImpl.calculateHistory();

        Mockito.verify(poolInfoService, times(1)).getPoolInfo();
        Mockito.verifyNoMoreInteractions(poolInfoService, poolHistoryDao);
    }

    @Test
    public void testUpdatePoolHistory() {
        var poolInfoOne = new PoolInfoDto();
        poolInfoOne.setOrganizationId(UUID.randomUUID().toString());
        poolInfoOne.setAvailablePoolSize(10);
        poolInfoOne.setDesiredPoolSize(11);

        var poolInfoTwo = new PoolInfoDto();
        poolInfoTwo.setOrganizationId(UUID.randomUUID().toString());
        poolInfoTwo.setAvailablePoolSize(12);
        poolInfoTwo.setDesiredPoolSize(13);

        Mockito.when(poolInfoService.getPoolInfo()).thenReturn(Arrays.asList(poolInfoOne, poolInfoTwo));

        poolHistoryServiceImpl.calculateHistory();

        Mockito.verify(poolInfoService, times(1)).getPoolInfo();
        var poolHistoryArgumentCaptor = ArgumentCaptor.forClass(PoolHistory.class);
        Mockito.verify(poolHistoryDao, times(2)).create(poolHistoryArgumentCaptor.capture());

        var allValues = poolHistoryArgumentCaptor.getAllValues();
        assertEquals(2, allValues.size());

        var firstEntry = allValues.getFirst();
        assertEquals(poolInfoOne.getOrganizationId(), firstEntry.getOrganisationCode());
        assertEquals(poolInfoOne.getAvailablePoolSize(), firstEntry.getAvailablePoolRooms().intValue());
        assertEquals(poolInfoOne.getDesiredPoolSize(), firstEntry.getDesiredPoolSize().intValue());
        assertNotNull(firstEntry.getStatusTime());

        var secondEntry = allValues.get(1);
        assertEquals(poolInfoTwo.getOrganizationId(), secondEntry.getOrganisationCode());
        assertEquals(poolInfoTwo.getAvailablePoolSize(), secondEntry.getAvailablePoolRooms().intValue());
        assertEquals(poolInfoTwo.getDesiredPoolSize(), secondEntry.getDesiredPoolSize().intValue());
        assertNotNull(secondEntry.getStatusTime());

        Mockito.verifyNoMoreInteractions(poolInfoService, poolHistoryDao);
    }
}
