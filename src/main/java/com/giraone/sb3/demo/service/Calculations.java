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

        final int ret;
        if (input < 3) {
            ret = 1;
        }
        else {
            ret = fibonacci(input - 1) + fibonacci(input - 2);
        }
        Observation.createNotStarted("demo.calculate", this.observationRegistry)
            .lowCardinalityKeyValue("input", Integer.toString(input))
            .observe(() -> ret);
        return ret;
    }
}
