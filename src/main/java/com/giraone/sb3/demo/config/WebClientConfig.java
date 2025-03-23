package com.giraone.sb3.demo.config;

import com.giraone.sb3.demo.service.CalculationWebClient;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    private final ApplicationProperties applicationProperties;

    public WebClientConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

    @Bean
    WebClient webClient(ObservationRegistry observationRegistry) {
        String host = "127.0.0.1";
        int port = 8080;
        if (applicationProperties.getClient() != null) {
            port = applicationProperties.getClient().getPort();
            host = applicationProperties.getClient().getHost();
        }
        if (port == 0) {
            port = 8080;
        }
        if (host == null || host.isEmpty()) {
            host = "127.0.0.1";
        }
        return WebClient.builder()
            .baseUrl("http://" + host + ":" + port)
            .observationRegistry(observationRegistry)
            .build();
    }

    // Just an example for "HTTP Interfaces in Spring " - see https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface
    @Bean
    CalculationWebClient calculationWebClient(WebClient webClient) {
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CalculationWebClient.class);
    }
}
