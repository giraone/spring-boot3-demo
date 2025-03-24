package com.giraone.sb3.demo.config;

import com.giraone.sb3.demo.controller.ServiceController;
import com.giraone.sb3.demo.controller.filter.SimpleRoutingFilter;
import com.giraone.sb3.demo.service.CalculationWebClient;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class WebClientConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebClientConfig.class);

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

    //--- Routing with WebFilter --- START ---

    @Bean
    public SimpleRoutingFilter simpleRoutingFilter() {
        LOGGER.info("Bean 'simpleRoutingFilter' created");
        return new SimpleRoutingFilter();
    }

    //--- Routing with WebFilter --- END ---

    //--- Routing with RouterFunction --- START ---

    @Bean
    public RouterFunction<ServerResponse> routingSample1(ServiceController serviceController) {
        LOGGER.info("Bean 'routingSample1' created");
        return route()
            .GET("sleep2", request -> callSleepBasedOnHeaderValue(serviceController, request.headers().header("mode")))
            .build();
    }

    static Mono<ServerResponse> callSleepBasedOnHeaderValue(ServiceController serviceController, List<String> modeHeaderValue) {
        final int value = extractSleepValue(modeHeaderValue);
        LOGGER.info("ROUTING from /sleep2 to /sleep/{}", value);
        return serviceController.sleep(value).flatMap(result -> ServerResponse.ok().body(result, Integer.class));
    }

    static int extractSleepValue(List<String> modeHeaderValue) {
        return !modeHeaderValue.isEmpty() && modeHeaderValue.getFirst().equals("short") ? 1000 : 10000;
    }

    //--- Routing with RouterFunction --- END ---
}
