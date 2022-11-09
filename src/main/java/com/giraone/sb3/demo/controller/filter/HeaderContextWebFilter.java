package com.giraone.sb3.demo.controller.filter;

import com.giraone.sb3.demo.config.ApplicationProperties;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class HeaderContextWebFilter extends HttpFilter {

    public static final ThreadLocal<Map<String, String>> THREAD_LOCAL_PROPAGATED_HEADERS = new ThreadLocal<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderContextWebFilter.class);

    private final Set<String> headers;
    private final Tracer tracer;

    public HeaderContextWebFilter(ApplicationProperties applicationProperties, Tracer tracer) {
        this.headers = applicationProperties.getPropagatedHeaders();
        this.tracer = tracer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String[] traceId = new String[] { null };
        String[] spanId = new String[] { null };
        if (tracer.currentSpan() != null && tracer.currentSpan().context() != null) {
            traceId[0] = tracer.currentSpan().context().traceId();
            spanId[0] = tracer.currentSpan().context().spanId();
        }
        if (headers == null) {
            LOGGER.warn("HeaderContextWebFilter NOT INITIALIZED! Cannot propagate HTTP Headers!");
        } else {
            final Map<String, String> headerMap = new HashMap<>();
            headers.forEach(headerName -> {
                final String headerValue = ((HttpServletRequest) request).getHeader(headerName);
                if (headerValue != null) {
                    headerMap.put(headerName, headerValue);
                } else {
                    if ("X-B3-TraceId".equals(headerName) && traceId[0] != null) {
                        headerMap.put(headerName, traceId[0]);
                    } else if ("X-B3-SpanId".equals(headerName) && spanId[0] != null) {
                        headerMap.put(headerName, spanId[0]);
                    }
                }
            });
            LOGGER.debug("HeaderContextWebFilter for propagation with {} entries", headerMap.size());
            THREAD_LOCAL_PROPAGATED_HEADERS.set(headerMap);
        }
        chain.doFilter(request, response);
    }
}
