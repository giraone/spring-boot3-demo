package com.giraone.sb3.demo.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalculationsTest {

    @Autowired
    private Calculations calculations;

    @ParameterizedTest
    @CsvSource({
        "1,1",
        "2,1",
        "3,2",
        "4,3",
        "5,5",
        "6,8",
        "10,55",
    })
    void fibonacci(int input, int expectedResult) {

        assertThat(calculations.fibonacci(input)).isEqualTo(expectedResult);
    }
}