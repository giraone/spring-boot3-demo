package com.giraone.sb3.demo.controller.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

// Only a test filter class, to show how routing/forward in Spring Boot WebFlux can be done
@Component
public class SimpleRoutingFilter implements WebFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleRoutingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        LOGGER.info("SimpleRoutingFilter.filter " + exchange.getRequest().getURI());
        if (exchange.getRequest().getURI().getPath().equals("/sleep3")) {
            return chain.filter(
                exchange.mutate()
                    .request(exchange.getRequest()
                        .mutate()
                        .path("/sleep/" + extractSleepValue(exchange.getRequest().getHeaders().get("mode")))
                        .build())
                    .build());
        }
        return chain.filter(exchange);
    }

    static int extractSleepValue(List<String> modeHeaderValue) {
        return !modeHeaderValue.isEmpty() && modeHeaderValue.getFirst().equals("short") ? 1000 : 10000;
    }
}
