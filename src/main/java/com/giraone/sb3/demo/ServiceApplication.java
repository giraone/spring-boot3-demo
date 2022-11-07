package com.giraone.sb3.demo;

import com.giraone.sb3.demo.config.ApplicationProperties;
import com.giraone.sb3.demo.service.CalculationWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class ServiceApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceApplication.class);

    private final ApplicationProperties applicationProperties;

    public ServiceApplication(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

    @Bean
    CalculationWebClient calculationWebClient(HttpServiceProxyFactory factory) {
        return factory.createClient(CalculationWebClient.class);
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {

        String host = "127.0.0.1";
        int port = 8080;
        if (applicationProperties.getService2() != null) {
            port = applicationProperties.getService2().port();
            host = applicationProperties.getService2().host();
        }
        if (port == 0) {
            port = 8080;
        }
        if (host == null || host.length() == 0) {
            host = "127.0.0.1";
        }
        WebClient.Builder webClientBuilder = builder.baseUrl("http://" + host + ":" + port);
        return WebClientAdapter.createHttpServiceProxyFactory(webClientBuilder);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        LOGGER.info("ServiceApplication READY");
        if (applicationProperties.isShowConfigOnStartup()) {
            LOGGER.info("{}", applicationProperties);
        }
    }
}
