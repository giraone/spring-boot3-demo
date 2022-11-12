package com.giraone.sb3.demo;

import com.giraone.sb3.demo.config.ApplicationProperties;
import com.giraone.sb3.demo.service.CalculationWebClient;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
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

;

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
    CalculationWebClient calculationWebClient() {
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
        WebClient webClient =  WebClient.builder()
            .baseUrl("http://" + host + ":" + port)
            .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
            .clientAdapter(WebClientAdapter.forClient(webClient))
            .build();
        return factory.createClient(CalculationWebClient.class);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        LOGGER.info("ServiceApplication READY");
        if (applicationProperties.isShowConfigOnStartup()) {
            LOGGER.info("{}", applicationProperties);
        }

        OpenTelemetrySdk openTelemetrySdk = AutoConfiguredOpenTelemetrySdk.initialize()
            .getOpenTelemetrySdk();
        LOGGER.info("{}", openTelemetrySdk);
    }
}
