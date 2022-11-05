package com.giraone.sb3.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProblemDetailErrorHandlerControllerAdvice {

    private static Logger LOGGER = LoggerFactory.getLogger(ProblemDetailErrorHandlerControllerAdvice.class);

    @ExceptionHandler
    ProblemDetail onException(IllegalArgumentException exception, HttpServletRequest request) {

        LOGGER.warn("Exception caught.", exception);
        // Shows access to jakarta package
        request.getAttributeNames().asIterator()
            .forEachRemaining(name -> LOGGER.warn("{}={}", name, request.getAttribute(name)));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }
}
