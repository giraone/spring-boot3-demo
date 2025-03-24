package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.ServiceApplication;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@AutoConfigureWebTestClient
class ServiceControllerIT {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceControllerIT.class);

    // Together with @DynamicPropertySource we fetch a random port and use it in the WebClient
    public static int PORT;

    static {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PORT = serverSocket.getLocalPort();
        LOGGER.info("Using WebClient with port {}", PORT);
    }

    @Autowired
    private WebTestClient webTestClient;

    // See https://www.baeldung.com/spring-dynamicpropertysource
    @SuppressWarnings("unused")
    @DynamicPropertySource
    static void portProperties(DynamicPropertyRegistry registry) {
        registry.add("server.port", () -> PORT);
        registry.add("application.client.port", () -> PORT);
    }

    @ParameterizedTest
    @CsvSource({
        "3,,true,2,0",
        "3,6,true,2,8",
    })
    void calculateOk(int input1, Integer input2, boolean ok, int value1, int value2) {
        // act/assert
        webTestClient
            .get()
            .uri("/calculate/{input1}?input2={input2}", input1, input2)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Content-Type", headerValue -> assertThat(headerValue).isEqualTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody(Result.class)
            .value(result -> {
                assertThat(result.ok()).isEqualTo(ok);
                assertThat(result.value1()).isEqualTo(value1);
                assertThat(result.value2()).isEqualTo(value2);
            });
    }

    @ParameterizedTest
    @CsvSource(value = {
        "3|0|RequestParam input2 must be null or between [1,30]",
        "0|1|PathVariable input1 must be between [1,30]",
    }, delimiter = '|')
    void calculateFails(int input1, Integer input2, String msg) {
        // act/assert
        webTestClient
            .get()
            .uri("/calculate/{input1}?input2={input2}", input1, input2)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest()
            .expectHeader().value("Content-Type", headerValue -> assertThat(headerValue).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE))
            .expectBody(ProblemDetail.class)
            .value(result -> {
                assertThat(result.getStatus()).isEqualTo(400);
                assertThat(result.getTitle()).isEqualTo("Bad Request");
                assertThat(result.getDetail()).isEqualTo(msg);
            });
    }

    @ParameterizedTest
    @CsvSource({
        "6,8",
    })
    void calculate2(int input, int value) {
        // act/assert
        webTestClient
            .get()
            .uri("/calculate2/{input}", input)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Content-Type", headerValue -> assertThat(headerValue).isEqualTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody(Integer.class)
            .value(result -> assertThat(result).isEqualTo(value));
    }

    @ParameterizedTest
    @CsvSource({
        "1000", "2000",
    })
    void sleep(long input) {

        // arrange
        long start = System.currentTimeMillis();
        // act
        webTestClient
            .get()
            .uri("/sleep/{input}", input)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Content-Type", headerValue -> assertThat(headerValue).isEqualTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody(Integer.class)
            .value(result -> assertThat(result).isEqualTo(input));
        // assert
        assertThat(System.currentTimeMillis() - start).isCloseTo(500 + input, Percentage.withPercentage(50));
    }
}