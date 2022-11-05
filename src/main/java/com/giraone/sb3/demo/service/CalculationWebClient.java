package com.giraone.sb3.demo.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface CalculationWebClient {

    @GetExchange("calculate2/{input}")
    int fibonacci(@PathVariable  int input);
}
