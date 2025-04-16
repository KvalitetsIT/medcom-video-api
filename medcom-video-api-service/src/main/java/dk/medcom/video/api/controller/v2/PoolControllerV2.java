package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.InternalServerErrorException;
import dk.medcom.video.api.controller.v2.mapper.PoolMapper;
import dk.medcom.video.api.interceptor.Oauth;
import dk.medcom.video.api.service.NewProvisionerOrganisationFilter;
import dk.medcom.video.api.service.PoolInfoServiceV2;
import org.openapitools.api.PoolV2Api;
import org.openapitools.model.PoolInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PoolControllerV2 implements PoolV2Api {
    private static final Logger logger = LoggerFactory.getLogger(PoolControllerV2.class);

    private final String anyRoleAtt = "hasAnyAuthority('ROLE_ATT_meeting-user','ROLE_ATT_meeting-admin','ROLE_ATT_meeting-provisioner','ROLE_ATT_meeting-provisioner-user','ROLE_ATT_meeting-planner')";

    private final PoolInfoServiceV2 poolInfoService;
    private final NewProvisionerOrganisationFilter provisionExcludeOrganisations;

    public PoolControllerV2(PoolInfoServiceV2 poolInfoService, NewProvisionerOrganisationFilter provisionExcludeOrganisations) {
        this.poolInfoService = poolInfoService;
        this.provisionExcludeOrganisations = provisionExcludeOrganisations;
    }

    @Oauth
    @Override
    @PreAuthorize(anyRoleAtt)
    public ResponseEntity<List<PoolInfo>> v2PoolGet() {
        logger.debug("Enter GET pool, v2.");
        try {
            var response = poolInfoService.getPoolInfoV2().stream().filter(x -> !provisionExcludeOrganisations.newProvisioner(x.organisationId())).collect(Collectors.toList());

            return ResponseEntity.ok(PoolMapper.internalToExternal(response));
        } catch (Exception e) {
            logger.error("Caught exception.", e);
            throw new InternalServerErrorException(e.getMessage());
        }

    }
}
