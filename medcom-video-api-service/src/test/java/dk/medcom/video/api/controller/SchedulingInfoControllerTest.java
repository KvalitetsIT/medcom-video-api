package dk.medcom.video.api.controller;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.Organisation;
import dk.medcom.video.api.dao.SchedulingInfo;
import dk.medcom.video.api.dto.CreateSchedulingInfoDto;
import dk.medcom.video.api.dto.SchedulingInfoDto;
import dk.medcom.video.api.helper.TestDataHelper;
import dk.medcom.video.api.service.SchedulingInfoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;

public class SchedulingInfoControllerTest {

    @Before
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testCreateSchedulingInfo() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setProvisionVmrId("vmr id");
        input.setSchedulingTemplateId(1L);
        input.setOrganizationId("pool-org");

        SchedulingInfoService schedulingInfoService = Mockito.mock(SchedulingInfoService.class);
        Organisation organisation = TestDataHelper.createOrganisation(true, "pool-org", 1L);
        SchedulingInfo expectedSchedulingInfoResult = TestDataHelper.createSchedulingInfo(organisation);
        Mockito.when(schedulingInfoService.createSchedulingInfo(input)).thenReturn(expectedSchedulingInfoResult);


        SchedulingInfoController controller = new SchedulingInfoController(schedulingInfoService);
        Resource<SchedulingInfoDto> result = controller.createSchedulingInfo(input);

        assertNotNull(result);
        assertNotNull(result.getContent());
        SchedulingInfoDto schedulingInfoDto = result.getContent();
        assertEquals("vmr id", schedulingInfoDto.getProvisionVmrId());
    }
}
