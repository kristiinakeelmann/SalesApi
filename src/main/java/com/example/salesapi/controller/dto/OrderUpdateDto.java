package com.example.salesapi.controller.dto;

import com.example.salesapi.model.enums.Status;
import lombok.Data;

@Data
public class OrderUpdateDto {

  private Status status;

}
