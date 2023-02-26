package com.giraone.sb3.demo.service;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

@Service
public class Calculations {

    private final ObservationRegistry observationRegistry;

    public Calculations(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public int fibonacci(int input) {

        final int ret = Observation.createNotStarted("demo.service.fibonacci", this.observationRegistry)
            .lowCardinalityKeyValue("input", Integer.toString(input))
            .observe(() -> fibonacciInternal(input));
        return ret;
    }

    private int fibonacciInternal(int input) {

        if (input < 3) {
            return 1;
        }
        else {
            return fibonacciInternal(input - 1) + fibonacciInternal(input - 2);
        }
    }
}
