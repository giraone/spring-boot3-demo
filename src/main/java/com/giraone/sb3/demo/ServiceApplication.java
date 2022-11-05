package com.giraone.sb3.demo;

import com.giraone.sb3.demo.service.CalculationWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class ServiceApplication {

    @Autowired
    private ServerProperties serverProperties;

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

    @Bean
    CalculationWebClient calculationWebClient(HttpServiceProxyFactory factory) {
        return factory.createClient(CalculationWebClient.class);
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {
        final int port = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;
        WebClient.Builder webClientBuilder = builder.baseUrl("http://localhost:" + port);
        return WebClientAdapter.createHttpServiceProxyFactory(webClientBuilder);
    }
}
