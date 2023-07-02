package com.giraone.sb3.demo.config;

import com.giraone.sb3.demo.service.CalculationWebClient;
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
    WebClient webClient() {
        String host = "127.0.0.1";
        int port = 8080;
        if (applicationProperties.getClient() != null) {
            port = applicationProperties.getClient().getPort();
            host = applicationProperties.getClient().getHost();
        }
        if (port == 0) {
            port = 8080;
        }
        if (host == null || host.length() == 0) {
            host = "127.0.0.1";
        }
        return WebClient.builder()
            .baseUrl("http://" + host + ":" + port)
            .build();
    }

    @Bean
    CalculationWebClient calculationWebClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
            .clientAdapter(WebClientAdapter.forClient(webClient))
            .build();
        return factory.createClient(CalculationWebClient.class);
    }
}
