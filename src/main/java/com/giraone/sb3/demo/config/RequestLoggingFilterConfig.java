package com.giraone.sb3.demo.config;

import com.giraone.sb3.demo.controller.filter.SimpleRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public SimpleRequestLoggingFilter logFilter() {
        SimpleRequestLoggingFilter filter = new SimpleRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(false);
        filter.setMaxPayloadLength(1000);
        // filter.setIncludeHeaders(true);
        return filter;
    }
}
