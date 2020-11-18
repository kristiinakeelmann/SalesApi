package com.example.salesapi.exception;

import com.example.salesapi.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

  @ExceptionHandler(value = SalesApiInvalidParametersException.class)
  private ResponseEntity<String> invalidParameters(Exception e) {
    log.warn("Invalid parameters", e);
    return ResponseEntity.badRequest().body("\"Invalid parameters\"");
  }

  @ExceptionHandler(value = SalesApiInvalidOrderStatusException.class)
  private ResponseEntity<String> invalidOrderStatus(Exception e) {
    log.warn("Invalid order status", e);
    return ResponseEntity.badRequest().body("\"Invalid order status\"");
  }

  @ExceptionHandler(value = SalesApiNotFoundException.class)
  private ResponseEntity<String> notFound(Exception e) {
    log.warn("Not found", e);
    return ResponseEntity.status(404).body("\"Not found\"");
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  private ResponseEntity<String> genericInvalidParameters(Exception e) {
    log.warn("Generic invalid parameters", e);
    return ResponseEntity.status(400).body("\"Invalid parameters\"");
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  private ResponseEntity<ErrorDto> genericNotFound(Exception e) {
    log.warn("Generic not found", e);
    ErrorDto.ErrorDetailDto errorDetailDto = new ErrorDto.ErrorDetailDto();
    errorDetailDto.setDetail("Not found");
    ErrorDto errorDto = new ErrorDto(errorDetailDto);
    return ResponseEntity.status(404).body(errorDto);
  }

  @ExceptionHandler(value = Exception.class)
  private ResponseEntity<ErrorDto> internalServerError(Exception e) {
    log.warn("Internal server error", e);
    ErrorDto.ErrorDetailDto errorDetailDto = new ErrorDto.ErrorDetailDto();
    errorDetailDto.setDetail("Internal Server Error");
    ErrorDto errorDto = new ErrorDto(errorDetailDto);
    return ResponseEntity.status(500).body(errorDto);
  }

}
