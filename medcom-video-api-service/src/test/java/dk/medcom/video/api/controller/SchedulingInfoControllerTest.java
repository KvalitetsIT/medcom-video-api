package dk.medcom.video.api.controller;

import dk.medcom.video.api.controller.exceptions.NotAcceptableException;
import dk.medcom.video.api.controller.exceptions.NotValidDataException;
import dk.medcom.video.api.controller.exceptions.PermissionDeniedException;
import dk.medcom.video.api.dao.entity.Organisation;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.api.CreateSchedulingInfoDto;
import dk.medcom.video.api.api.SchedulingInfoDto;
import dk.medcom.video.api.helper.TestDataHelper;
import dk.medcom.video.api.service.SchedulingInfoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.EntityModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

public class SchedulingInfoControllerTest {

    @Before
    public void setup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testCreateSchedulingInfo() throws NotValidDataException, PermissionDeniedException, NotAcceptableException {
        CreateSchedulingInfoDto input = new CreateSchedulingInfoDto();
        input.setSchedulingTemplateId(1L);
        input.setOrganizationId("pool-org");

        SchedulingInfoServiceImpl schedulingInfoService = Mockito.mock(SchedulingInfoServiceImpl.class);
        Organisation organisation = TestDataHelper.createOrganisation(true, "pool-org", 1L);
        SchedulingInfo expectedSchedulingInfoResult = TestDataHelper.createSchedulingInfo(organisation);
        expectedSchedulingInfoResult.setProvisionVMRId(null);
        Mockito.when(schedulingInfoService.createSchedulingInfo(input)).thenReturn(expectedSchedulingInfoResult);

        SchedulingInfoController controller = new SchedulingInfoController(schedulingInfoService, "base_url");
        EntityModel<SchedulingInfoDto> result = controller.createSchedulingInfo(input);

        assertNotNull(result);
        assertNotNull(result.getContent());
        SchedulingInfoDto schedulingInfoDto = result.getContent();
        assertNull(schedulingInfoDto.getProvisionVmrId());
        assertTrue("Host pin must be greater than 0.", schedulingInfoDto.getHostPin() > 0);
    }
}
