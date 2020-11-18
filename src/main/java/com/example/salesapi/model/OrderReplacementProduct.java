package com.example.salesapi.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Entity
@Table(name="repl_prod")
public class OrderReplacementProduct {

  @Id
  @GeneratedValue(generator = "uuid")
  private UUID id;
  private int quantity;
  private int productId;
  private UUID orderProductId;

}
