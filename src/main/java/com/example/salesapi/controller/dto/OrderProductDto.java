package com.example.salesapi.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderProductDto {

  private String id;
  private String name;
  private BigDecimal price;
  private Integer product_id;
  private int quantity;
  private OrderProductDto replaced_with;

}
