package com.example.salesapi.controller;

import com.example.salesapi.SalesApiBadRequestException;
import com.example.salesapi.SalesApiNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

  @ExceptionHandler(value = SalesApiBadRequestException.class)
  private ResponseEntity<String> invalidParameters(Exception e) {
    log.warn("Invalid parameter", e);
    return ResponseEntity.badRequest().body("\"Invalid parameters\"");
  }

  @ExceptionHandler(value = SalesApiNotFoundException.class)
  private ResponseEntity<String> notFound(Exception e) {
    log.warn("Not found", e);
    return ResponseEntity.status(404).body("\"Not found\"");
  }

}
