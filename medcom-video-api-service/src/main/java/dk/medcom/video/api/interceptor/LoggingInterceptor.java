package dk.medcom.video.api.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class LoggingInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
	
	public static final String MDC_CORRELATION_ID = "correlation-id";
	public static final String MDC_REQUEST_URL = "request-url";
	
	@Value("${CORRELATION.ID:correlation-id}")
	private String correlationIdHeaderName;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		String correlationId = request.getHeader(correlationIdHeaderName);
		LOGGER.debug("Extracted header: "+correlationIdHeaderName+" with value:"+correlationId);
		if(correlationId == null) {
			correlationId = UUID.randomUUID().toString();
			LOGGER.debug("Generated new correlation id as it was not found in header: {}.", correlationId);
		}
		MDC.put(MDC_CORRELATION_ID, correlationId);

		String requestUrl = request.getRequestURL().toString();
		MDC.put(MDC_REQUEST_URL, requestUrl);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		MDC.clear();
	}
}