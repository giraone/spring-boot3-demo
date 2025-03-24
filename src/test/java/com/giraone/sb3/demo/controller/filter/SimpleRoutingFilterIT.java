package com.giraone.sb3.demo.controller.filter;

import com.giraone.sb3.demo.ServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SimpleRoutingFilterIT {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenSleep3IsCalled_thenWebFilterIsApplied() {
        EntityExchangeResult<Integer> result = webTestClient.get()
            .uri("/sleep3")
            .exchange()
            .expectStatus().isOk()
            .expectBody(Integer.class)
            .returnResult();

        assertEquals(result.getResponseBody(), "x");
        assertEquals(
            result.getResponseHeaders().getFirst("web-filter"),
            "web-filter-test");
    }

}