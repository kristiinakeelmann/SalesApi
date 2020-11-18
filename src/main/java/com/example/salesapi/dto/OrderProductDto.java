package com.example.salesapi.dto;

import lombok.Data;

@Data
public class OrderProductDto {

  private String id;
  private String name;
  private String price;
  private Integer product_id;
  private int quantity;
  private OrderProductDto replaced_with;

}
