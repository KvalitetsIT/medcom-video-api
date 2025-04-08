package dk.medcom.video.api.controller.v2;

import dk.medcom.video.api.controller.v2.exception.InternalServerErrorException;
import dk.medcom.video.api.controller.v2.mapper.InfoMapper;
import dk.medcom.video.api.interceptor.Oauth;
import org.openapitools.api.InfoV2Api;
import org.openapitools.model.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info.Builder;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InfoControllerV2 implements InfoV2Api {
    private static final Logger logger = LoggerFactory.getLogger(InfoControllerV2.class);

    private final String anyScope = "hasAnyAuthority('SCOPE_meeting-user','SCOPE_meeting-admin','SCOPE_meeting-provisioner','SCOPE_meeting-provisioner-user','SCOPE_meeting-planner','SCOPE_undefined')";

    private final List<InfoContributor> infoContributors;

    public InfoControllerV2(List<InfoContributor> infoContributors) {
        this.infoContributors = infoContributors;
    }

    @Oauth
    @Override
    @PreAuthorize(anyScope)
    public ResponseEntity<Info> v2InfoGet() {
        logger.debug("Enter GET info, v2");
        try {
            Builder builder = new Builder();

            infoContributors.forEach(x -> x.contribute(builder));

            org.springframework.boot.actuate.info.Info build = builder.build();
            return ResponseEntity.ok(InfoMapper.internalToExternal(build));
        } catch (Exception e) {
            logger.error("Caught unexpected exception.", e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
