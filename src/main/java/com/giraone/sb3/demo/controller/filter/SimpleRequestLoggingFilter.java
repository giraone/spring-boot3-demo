package com.giraone.sb3.demo.controller.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

public class SimpleRequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        final String path = request.getRequestURI();
//        final Enumeration<String> enums = request.getHeaderNames();
//        enums.asIterator().forEachRemaining(h -> {
//            String value = request.getHeader(h);
//            System.out.println(h + " " + value);
//        });
        return logger.isDebugEnabled() && path != null && !path.startsWith("/actuator");
    }

    /**
     * Writes a log message before the request is processed.
     */
    @Override
    protected void beforeRequest(@NonNull HttpServletRequest request, @NonNull String message) {
        logger.debug(message);
    }

    /**
     * Writes a log message after the request is processed.
     */
    @Override
    protected void afterRequest(@NonNull HttpServletRequest request, @NonNull  String message) {
        // Intentionally left blank
    }
}
