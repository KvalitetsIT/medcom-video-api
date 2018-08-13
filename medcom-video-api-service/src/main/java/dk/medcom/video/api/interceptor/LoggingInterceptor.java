package dk.medcom.video.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoggingInterceptor extends HandlerInterceptorAdapter {

	private static Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
	
	public static final String MDC_CORRELATION_ID = "correlation-id";
	public static final String MDC_REQUEST_URL = "request-url";
	
	@Value("${correlationid.httpheader.name}")
	private String correlationIdHeaderName;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String correlationId = request.getHeader(correlationIdHeaderName);
		LOGGER.debug("Extracted header: "+correlationIdHeaderName+" with value:"+correlationId);
		if (correlationId != null) {
			MDC.put(MDC_CORRELATION_ID, correlationId);
		}
		String requestUrl = request.getRequestURL().toString();
		if (requestUrl != null) {
			MDC.put(MDC_REQUEST_URL, requestUrl);
		}
		return true;
	}
}