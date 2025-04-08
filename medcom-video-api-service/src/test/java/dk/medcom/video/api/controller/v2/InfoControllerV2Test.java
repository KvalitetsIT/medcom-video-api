package dk.medcom.video.api.controller.v2;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.info.InfoContributor;

import java.util.List;

import static org.junit.Assert.*;

public class InfoControllerV2Test {

    private InfoControllerV2 infoControllerV2;

    @Before
    public void setup() {
        var infoContributor = Mockito.mock(InfoContributor.class);
        var infoContributors = List.of(infoContributor);
        infoControllerV2 = new InfoControllerV2(infoContributors);
    }

    @Test
    public void testV2InfoGet() {
        var result = infoControllerV2.v2InfoGet();
        assertNotNull(result);
        assertEquals(200, result.getStatusCode().value());

        assertNotNull(result.getBody());
        assertNull(result.getBody().getInfo());
    }
}
