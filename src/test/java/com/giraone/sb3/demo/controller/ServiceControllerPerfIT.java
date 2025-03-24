package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.ServiceApplication;
import com.jupiter.tools.stress.test.concurrency.ExecutionMode;
import com.jupiter.tools.stress.test.concurrency.StressTestRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ServiceControllerPerfIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void concurrentThreadsSubTasks() {
        StressTestRunner.test()
            .mode(ExecutionMode.PARALLEL_STREAM_MODE)
            .timeout(5, TimeUnit.SECONDS)
            .threads(4)
            .iterations(100)
            .run(this::sleep);
    }

    private void sleep() {
        int input = 100;
        webTestClient
            .get()
            .uri("/sleep/{input}", input)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().value("Content-Type", headerValue -> assertThat(headerValue).isEqualTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody(Integer.class)
            .value(result -> assertThat(result).isEqualTo(input));
    }
}