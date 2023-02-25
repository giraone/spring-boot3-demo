package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.ServiceApplication;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}