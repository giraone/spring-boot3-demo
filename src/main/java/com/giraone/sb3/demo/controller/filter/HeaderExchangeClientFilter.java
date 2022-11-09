package com.giraone.sb3.demo.controller.filter;

import com.giraone.sb3.demo.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@Component
public class HeaderExchangeClientFilter implements ExchangeFilterFunction {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeaderExchangeClientFilter.class);

    private final Set<String> headers;

    public HeaderExchangeClientFilter(ApplicationProperties applicationProperties) {
        this.headers = applicationProperties.getPropagatedHeaders();
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {

        Map<String, String> headerMap = HeaderContextWebFilter.THREAD_LOCAL_PROPAGATED_HEADERS.get();

        if (headerMap == null) {
            LOGGER.warn("No \"HeaderContextWebFilter.THREAD_LOCAL_PROPAGATED_HEADERS\" found in thread context!");
            return exchangeFunction.exchange(clientRequest);
        }

        ClientRequest newRequest = ClientRequest
            .from(clientRequest)
            .headers(httpHeaders -> headers.forEach(header -> {
                final String headerValue = headerMap.get(header);
                if (headerValue != null) {
                    LOGGER.debug("HeaderExchange {} -> {}", header, headerValue);
                    httpHeaders.add(header, headerValue);
                }
            }))
            .build();
        return exchangeFunction.exchange(newRequest);
    }
}