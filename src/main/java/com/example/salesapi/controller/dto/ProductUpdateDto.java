package com.example.salesapi.controller.dto;

import lombok.Data;

@Data
public class ProductUpdateDto {

  private Integer quantity;
  private ReplacedWithDto replaced_with;

  @Data
  public static class ReplacedWithDto {
    private int product_id;
    private int quantity;
  }

}
