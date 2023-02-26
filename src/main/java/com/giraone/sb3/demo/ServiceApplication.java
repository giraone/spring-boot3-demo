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
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

@SpringBootApplication
public class ServiceApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceApplication.class);

    private final ApplicationProperties applicationProperties;

    public ServiceApplication(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServiceApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
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

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        LOGGER.info("ServiceApplication READY");
        if (applicationProperties.isShowConfigOnStartup()) {
            LOGGER.info("{}", applicationProperties);
        }
/*
        OpenTelemetrySdk openTelemetrySdk = AutoConfiguredOpenTelemetrySdk.initialize()
            .getOpenTelemetrySdk();
        LOGGER.info("{}", openTelemetrySdk);*/
    }

    private static void logApplicationStartup(Environment env) {

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtils.hasText(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOGGER.info("""
                ----------------------------------------------------------
                ~~~ Application '{}' is running! Access URLs:
                ~~~ - Local:      {}://localhost:{}{}
                ~~~ - External:   {}://{}:{}{}
                ~~~ Java version:      {} / {}
                ~~~ Processors:        {}
                ~~~ Profile(s):        {}
                ~~~ Default charset:   {}
                ~~~ File encoding:     {}
                ----------------------------------------------------------""",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            System.getProperty("java.version"), System.getProperty("java.vm.name"),
            Runtime.getRuntime().availableProcessors(),
            env.getActiveProfiles(),
            Charset.defaultCharset().displayName(),
            System.getProperty("file.encoding")
        );
    }
}
