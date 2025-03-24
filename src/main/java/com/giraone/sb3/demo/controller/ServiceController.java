package com.giraone.sb3.demo.controller;

import com.giraone.sb3.demo.service.CalculationWebClient;
import com.giraone.sb3.demo.service.Calculations;
import io.micrometer.observation.annotation.Observed;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class ServiceController {

    private final Calculations calculations;
    private final CalculationWebClient calculationWebClient;

    public ServiceController(Calculations calculations, CalculationWebClient calculationWebClient) {
        this.calculations = calculations;
        this.calculationWebClient = calculationWebClient;
    }

    @Observed(
        name = "demo.controller.endpoints"
    )
    @GetMapping("calculate/{input}")
    Result calculate(@PathVariable int input, @RequestParam(required = false) Integer input2) {

        if (input < 1 || input > 30) {
            throw new IllegalArgumentException("PathVariable input1 must be between [1,30]");
        }

        if (input2 != null) {
            if (input2 < 1 || input2 > 30) {
                throw new IllegalArgumentException("RequestParam input2 must be null or between [1,30]");
            }
        }

        int result1 = calculations.fibonacci(input);
        int result2 = 0;
        if (input2 != null) {
            result2 = calculationWebClient.fibonacci(input2);
        }

        return new Result(true, result1, result2);
    }

    @Observed(
        name = "demo.controller.endpoints"
    )
    @GetMapping("calculate2/{input}")
    int calculate2(@PathVariable int input) {

        if (input < 1 || input > 30) {
            throw new IllegalArgumentException("PathVariable input must be between [1,30]");
        }
        return calculations.fibonacci(input);
    }

    @Observed(
        name = "demo.controller.sleep",
        contextualName = "observed sleep endpoint",
        lowCardinalityKeyValues = {
            "class", "ServiceController"
        }
    )
    @GetMapping("sleep/{input}")
    public Mono<Integer> sleep(@PathVariable int input) {
        if (input < 0) {
            input = 0;
        }
        return Mono.delay(Duration.ofMillis(input)).then(Mono.just(input));
    }
}
