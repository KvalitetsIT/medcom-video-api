package dk.medcom.video.api.controller.advice;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class RequestBodyFieldMetricAdvice extends RequestBodyAdviceAdapter {
    private final static Logger logger = LoggerFactory.getLogger(RequestBodyFieldMetricAdvice.class);

    private final MeterRegistry meterRegistry;

    public RequestBodyFieldMetricAdvice(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return isPostRequest(Objects.requireNonNull(methodParameter.getMethod()).getName());
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        var method = Objects.requireNonNull(parameter.getMethod()).getName();
        countFields(method, body);

        return body;
    }

    private boolean isPostRequest(String method) {
        return method.contains("Post") || method.equals("createMeeting") || method.equals("createSchedulingInfo") || method.equals("createSchedulingTemplate");
    }

    private void countFields(String method, Object body) {
        Field[] fields = body.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.get(body) != null) {
                    if (field.getType().getSimpleName().equals("List")) {
                        var fieldAsList = (List<?>) field.get(body);
                        Counter.builder("api.request.fields.usage")
                                .tag("method", method)
                                .tag("object", body.getClass().getSimpleName())
                                .tag("field", field.getName())
                                .tag("type", field.getType().getSimpleName() + "_isEmpty_" + fieldAsList.isEmpty())
                                .register(meterRegistry)
                                .increment();
                    } else {
                        Counter.builder("api.request.fields.usage")
                                .tag("method", method)
                                .tag("object", body.getClass().getSimpleName())
                                .tag("field", field.getName())
                                .tag("type", field.getType().getSimpleName())
                                .register(meterRegistry)
                                .increment();
                    }
                }
            } catch (Exception e) {
                logger.warn("Caught exception", e);
            }
        }
    }
}
