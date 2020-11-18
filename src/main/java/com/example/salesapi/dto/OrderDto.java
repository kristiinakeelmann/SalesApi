package com.example.salesapi.dto;

import com.example.salesapi.model.enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {

  private AmountDto amount;
  private String id;
  private List<OrderProductDto> products;
  private Status status;

}
