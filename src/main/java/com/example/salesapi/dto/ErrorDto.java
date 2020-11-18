package com.example.salesapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {

  private ErrorDetailDto errors;

  @Data
  public static class ErrorDetailDto {
    private String detail;
  }

}
