package com.example.salesapi.model;

import com.example.salesapi.model.enums.Status;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class SalesOrder {

  @Id
  @GeneratedValue(generator = "uuid")
  private UUID id;
  private Status status;
  private BigDecimal discount;
  private BigDecimal paid;
  private BigDecimal returns;
  private BigDecimal total;

}
