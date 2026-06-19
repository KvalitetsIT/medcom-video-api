package dk.medcom.video.api.service.model;

import dk.medcom.video.api.dao.entity.GuestMicrophone;
import dk.medcom.video.api.dao.entity.SchedulingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public record PortalLinkModel(String videoPortal,
                              List<QueryParameter> queryParameters,
                              String fragment) {

    public record QueryParameter(String key, QueryParameterValue value) {
        public static QueryParameter fromKeyValue(String key, String value) {
            return new QueryParameter(key, QueryParameterValue.fromString(value));
        }
    }

    public static PortalLinkModel fromTemplate(String template) {
        var uriComponents = UriComponentsBuilder.fromUriString(template).build();

        var baseUri = UriComponentsBuilder.newInstance()
                .scheme(uriComponents.getScheme())
                .host(uriComponents.getHost())
                .port(uriComponents.getPort())
                .path(Objects.requireNonNull(uriComponents.getPath()))
                .toUriString();

        var queryParams = uriComponents.getQueryParams().entrySet().stream()
                .map(entry -> QueryParameter.fromKeyValue(entry.getKey(), entry.getValue().getFirst()))
                .toList();

        return new PortalLinkModel(baseUri, queryParams, uriComponents.getFragment());
    }

    public sealed interface QueryParameterValue permits PortalLinkPlaceholder, QueryParameterConstant {
        Map<String, PortalLinkPlaceholder> placeholderMap = Arrays.stream(PortalLinkPlaceholder.values())
                .collect(Collectors.toMap(PortalLinkPlaceholder::getPlaceHolder, p -> p));

        static QueryParameterValue fromString(String value) {
            PortalLinkPlaceholder placeholder = placeholderMap.get(value);
            return placeholder != null ? placeholder : new QueryParameterConstant(value);
        }
    }

    public record QueryParameterConstant(String value) implements QueryParameterValue {
    }

    public enum PortalLinkPlaceholder implements QueryParameterValue {
        PIN("__pin__", PortalLinkPlaceholder::mapPin),
        URI_WITH_DOMAIN("__uri-with-domain__", PortalLinkPlaceholder::mapUriWithDomain),
        URI_DOMAIN("__uri-domain__", PortalLinkPlaceholder::mapUriDomain),
        START_DATE("__start-date__", PortalLinkPlaceholder::mapStartDate),
        RETURN_URL("__return-url__", PortalLinkPlaceholder::mapReturnUrl),
        MICROPHONE("__microphone__", PortalLinkPlaceholder::mapMicrophone),
        CALL_TYPE("__call-type__", PortalLinkPlaceholder::mapCallType)
        ;

        private final static Logger logger = LoggerFactory.getLogger(PortalLinkPlaceholder.class);
        private final String placeHolder;
        private final BiFunction<Date, SchedulingInfo, Optional<String>> extractValue;

        PortalLinkPlaceholder(String placeHolder, BiFunction<Date, SchedulingInfo, Optional<String>> extractValue) {
            this.placeHolder = placeHolder;
            this.extractValue = extractValue;
        }

        public String getPlaceHolder() {
            return placeHolder;
        }

        public Optional<String> extractValue(Date startTime, SchedulingInfo schedulingInfo) {
            return extractValue.apply(startTime, schedulingInfo);
        }

        private static Optional<String> mapPin(Date startTime, SchedulingInfo schedulingInfo) {
            if (schedulingInfo.getGuestPin() != null) {
                logger.debug("Portal pin used is guest");
                return Optional.of(schedulingInfo.getGuestPin().toString());
            }
            else if (schedulingInfo.getHostPin() != null) {
                logger.debug("Portal pin used is host");
                return Optional.of(schedulingInfo.getHostPin().toString());
            }
            else {
                logger.debug("Portal pin used is empty");
                return Optional.of("");
            }
        }

        private static Optional<String> mapUriWithDomain(Date startTime, SchedulingInfo schedulingInfo) {
            return Optional.ofNullable(schedulingInfo.getUriWithDomain());
        }

        private static Optional<String> mapUriDomain(Date startTime, SchedulingInfo schedulingInfo) {
            return Optional.ofNullable(schedulingInfo.getUriDomain());
        }

        private static Optional<String> mapReturnUrl(Date startTime, SchedulingInfo schedulingInfo) {
            return Optional.ofNullable(schedulingInfo.getReturnUrl());
        }

        private static Optional<String> mapStartDate(Date startTime, SchedulingInfo schedulingInfo) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return Optional.of(formatter.format(startTime));
        }

        private static Optional<String> mapMicrophone(Date date, SchedulingInfo schedulingInfo) {
            if (schedulingInfo.getMeeting() != null && schedulingInfo.getMeeting().getGuestMicrophone() != null){
                logger.debug("Guest microphone is: {}", schedulingInfo.getMeeting().getGuestMicrophone());
                if (schedulingInfo.getMeeting().getGuestMicrophone() != GuestMicrophone.on){
                    return Optional.of(schedulingInfo.getMeeting().getGuestMicrophone().toString().toLowerCase());
                }
            } else {
                logger.debug("Guest microphone is not set");
            }
            return Optional.empty();
        }

        private static Optional<String> mapCallType(Date date, SchedulingInfo schedulingInfo) {
            return Optional.ofNullable(schedulingInfo.getCallType());
        }
    }
}