package com.giraone.sb3.demo;

import com.giraone.sb3.demo.service.CalculationWebClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class ServiceApplication1 {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication1.class, args);
	}

    @Bean
    CalculationWebClient calculationWebClient(HttpServiceProxyFactory factory) {
        return factory.createClient(CalculationWebClient.class);
    }

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(WebClient.Builder builder) {
        var wc = builder.baseUrl("http://localhost:8080");
        return WebClientAdapter.createHttpServiceProxyFactory(wc);
    }
}
