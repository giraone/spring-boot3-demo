package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.ServiceApplication;
import com.jupiter.tools.stress.test.concurrency.ExecutionMode;
import com.jupiter.tools.stress.test.concurrency.StressTestRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ServiceControllerPerfTest {

    @Autowired
    private MockMvc mockMvc;

    @Disabled
    @Test
    void concurrentThreadsSubTasks() {
        StressTestRunner.test()
            .mode(ExecutionMode.PARALLEL_STREAM_MODE)
            .timeout(5, TimeUnit.SECONDS)
            .threads(4)
            .iterations(100)
            .run(this::sleep);
    }

    private void sleep() throws Exception {

        int input = 100;
        mockMvc.perform(get("/sleep/{input}", input)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(input))
        ;
    }
}