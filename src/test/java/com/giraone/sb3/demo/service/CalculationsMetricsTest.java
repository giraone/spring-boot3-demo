package com.giraone.sb3.demo.service;

import io.micrometer.observation.tck.TestObservationRegistry;
import io.micrometer.observation.tck.TestObservationRegistryAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalculationsMetricsTest {

    private Calculations calculations;
    private TestObservationRegistry registry;

    @BeforeEach
    void setup() {
        registry = TestObservationRegistry.create();
        calculations = new Calculations(registry);
    }

    @Test
    void fibonacciHasMetric() {

        calculations.fibonacci(10);

        TestObservationRegistryAssert.assertThat(registry)
            .doesNotHaveAnyRemainingCurrentObservation()
            .hasObservationWithNameEqualTo("demo.service.fibonacci")
            .that()
            .hasLowCardinalityKeyValue("input","10")
            .hasBeenStarted()
            .hasBeenStopped();
    }
}