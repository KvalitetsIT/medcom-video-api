package dk.medcom.video.api.service;

import dk.medcom.video.api.dao.entity.SchedulingInfo;
import dk.medcom.video.api.service.model.PortalLinkModel;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.Optional;

public class PortalLinkBuilder {
    private final PortalLinkModel templateModel;

    public PortalLinkBuilder(String portalLinkTemplate) {
        this.templateModel = PortalLinkModel.fromTemplate(portalLinkTemplate);
    }

    public String buildPortalLink(Date startTime, SchedulingInfo schedulingInfo) {
        var baseWithValues = replacePlaceholders(templateModel.videoPortal(), startTime, schedulingInfo);
        var uriBuilder = UriComponentsBuilder.fromUriString(baseWithValues);

        // Query parameters
        templateModel.queryParameters().forEach(param -> resolveValue(param.value(), startTime, schedulingInfo)
                .ifPresent(val -> uriBuilder.queryParam(param.key(), val)));

        // Fragment
        if (templateModel.fragment() != null) {
            var fragmentWithValues = replacePlaceholders(templateModel.fragment(), startTime, schedulingInfo);
            uriBuilder.fragment(fragmentWithValues);
        }

        return uriBuilder.build().toUriString();
    }

    private String replacePlaceholders(String text, Date startTime, SchedulingInfo schedulingInfo) {
        String result = text;
        for (PortalLinkModel.PortalLinkPlaceholder placeholder : PortalLinkModel.PortalLinkPlaceholder.values()) {
            var val = placeholder.extractValue(startTime, schedulingInfo).orElse("");
            result = result.replace(placeholder.getPlaceHolder(), val);
        }
        return result;
    }

    private Optional<String> resolveValue(PortalLinkModel.QueryParameterValue paramValue, Date startTime, SchedulingInfo schedulingInfo) {
        return switch (paramValue) {
            case PortalLinkModel.QueryParameterConstant constant -> Optional.of(constant.value());
            case PortalLinkModel.PortalLinkPlaceholder placeholder -> placeholder.extractValue(startTime, schedulingInfo);
        };
    }
}