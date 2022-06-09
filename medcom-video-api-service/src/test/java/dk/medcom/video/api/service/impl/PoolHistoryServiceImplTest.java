package dk.medcom.video.api.service.impl;

import dk.medcom.video.api.dao.PoolHistoryDao;
import dk.medcom.video.api.dao.PoolInfoRepository;
import dk.medcom.video.api.dao.entity.PoolHistory;
import dk.medcom.video.api.entity.PoolInfoEntity;
import dk.medcom.video.api.service.PoolHistoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;

public class PoolHistoryServiceImplTest {
    private PoolHistoryServiceImpl poolHistoryServiceImpl;
    private PoolInfoRepository poolInfoRepository;
    private PoolHistoryDao poolHistoryDao;

    @Before
    public void setup() {
        poolInfoRepository = Mockito.mock(PoolInfoRepository.class);
        poolHistoryDao = Mockito.mock(PoolHistoryDao.class);

        poolHistoryServiceImpl = new PoolHistoryServiceImpl(poolInfoRepository, poolHistoryDao);
    }

    @Test
    public void testNoPools() {
        Mockito.when(poolInfoRepository.getPoolInfos()).thenReturn(Collections.emptyList());

        poolHistoryServiceImpl.calculateHistory();

        Mockito.verify(poolInfoRepository, times(1)).getPoolInfos();
        Mockito.verifyNoMoreInteractions(poolInfoRepository, poolHistoryDao);
    }

    @Test
    public void testUpdatePoolHistory() {
        var poolInfoEntityOne = new PoolInfoEntity();
        poolInfoEntityOne.setOrganisationCode(UUID.randomUUID().toString());
        poolInfoEntityOne.setAvailablePoolSize(10);
        poolInfoEntityOne.setWantedPoolSize(11);

        var poolInfoEntityTwo = new PoolInfoEntity();
        poolInfoEntityTwo.setOrganisationCode(UUID.randomUUID().toString());
        poolInfoEntityTwo.setAvailablePoolSize(12);
        poolInfoEntityTwo.setWantedPoolSize(13);

        Mockito.when(poolInfoRepository.getPoolInfos()).thenReturn(Arrays.asList(poolInfoEntityOne, poolInfoEntityTwo));

        poolHistoryServiceImpl.calculateHistory();

        Mockito.verify(poolInfoRepository, times(1)).getPoolInfos();
        var poolHistoryArgumentCaptor = ArgumentCaptor.forClass(PoolHistory.class);
        Mockito.verify(poolHistoryDao, times(2)).create(poolHistoryArgumentCaptor.capture());

        var allValues = poolHistoryArgumentCaptor.getAllValues();
        assertEquals(2, allValues.size());

        var firstEntry = allValues.get(0);
        assertEquals(poolInfoEntityOne.getOrganisationCode(), firstEntry.getOrganisationCode());
        assertEquals(poolInfoEntityOne.getAvailablePoolSize(), firstEntry.getAvailablePoolRooms().intValue());
        assertEquals(poolInfoEntityOne.getWantedPoolSize(), firstEntry.getDesiredPoolSize().intValue());
        assertNotNull(firstEntry.getStatusTime());

        var secondEntry = allValues.get(1);
        assertEquals(poolInfoEntityTwo.getOrganisationCode(), secondEntry.getOrganisationCode());
        assertEquals(poolInfoEntityTwo.getAvailablePoolSize(), secondEntry.getAvailablePoolRooms().intValue());
        assertEquals(poolInfoEntityTwo.getWantedPoolSize(), secondEntry.getDesiredPoolSize().intValue());
        assertNotNull(secondEntry.getStatusTime());

        Mockito.verifyNoMoreInteractions(poolInfoRepository, poolHistoryDao);
    }
}
