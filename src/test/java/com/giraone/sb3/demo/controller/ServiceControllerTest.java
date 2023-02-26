package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.ServiceApplication;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
@AutoConfigureMockMvc
class ServiceControllerTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceControllerTest.class);

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
    private MockMvc mockMvc;

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
    void calculateOk(int input1, Integer input2, boolean ok, int value1, int value2) throws Exception {

        // act/assert
        mockMvc.perform(get("/calculate/{input1}?input2={input2}", input1, input2)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.ok").value(ok))
            .andExpect(jsonPath("$.value1").value(value1))
            .andExpect(jsonPath("$.value2").value(value2))
        ;
    }

    @ParameterizedTest
    @CsvSource(value = {
        "3|0|RequestParam input2 must be null or between [1,30]",
        "0|1|PathVariable input1 must be between [1,30]",
    }, delimiter = '|')
    void calculateFails(int input1, Integer input2, String msg) throws Exception {

        // act/assert
        mockMvc.perform(get("/calculate/{input1}?input2={input2}", input1, input2)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.detail").value(msg))
        ;
    }

    @ParameterizedTest
    @CsvSource({
        "6,8",
    })
    void calculate2(int input, int value) throws Exception {

        // act/assert
        mockMvc.perform(get("/calculate2/{input}", input)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(value))
        ;
    }

    @ParameterizedTest
    @CsvSource({
        "1000", "2000",
    })
    void sleep(long input) throws Exception {

        // arrange
        long start = System.currentTimeMillis();
        // act
        mockMvc.perform(get("/sleep/{input}", input)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(input));
        // assert
        assertThat(System.currentTimeMillis() - start).isCloseTo(input, Percentage.withPercentage(50));
    }
}