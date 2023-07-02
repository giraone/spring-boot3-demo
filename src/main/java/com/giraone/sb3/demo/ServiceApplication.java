package com.giraone.sb3.demo;

import com.giraone.sb3.demo.config.ApplicationProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

@SpringBootApplication
public class ServiceApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceApplication.class);

    private final ApplicationProperties applicationProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    public ServiceApplication(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServiceApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
	}

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", applicationName);
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
