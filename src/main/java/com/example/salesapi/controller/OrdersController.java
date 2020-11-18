package com.example.salesapi.controller;

import com.example.salesapi.dto.OrderDto;
import com.example.salesapi.dto.OrderUpdateDto;
import com.example.salesapi.service.OrderCommandService;
import com.example.salesapi.service.OrderQueryService;
import com.example.salesapi.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class OrdersController {

  private final OrderCommandService orderCommandService;
  private final OrderQueryService orderQueryService;
  private final ValidatorUtil validatorUtil;

  @PostMapping(value = "/orders", produces = "application/json")
  private ResponseEntity<OrderDto> createOrder() {
    UUID salesOrderId = orderCommandService.createSalesOrder();
    return ResponseEntity.status(201).body(orderQueryService.getSalesOrder(salesOrderId));
  }

  @GetMapping(value = "/orders/{id}", produces = "application/json")
  private ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
    UUID uuid = validatorUtil.validateAndParseUUID(id);
    validatorUtil.orderExists(uuid);
    return ResponseEntity.ok(orderQueryService.getSalesOrder(uuid));
  }

  @PatchMapping(value = "/orders/{id}", produces = "application/json")
  private ResponseEntity<String> updateOrder(@PathVariable String id, @RequestBody OrderUpdateDto orderUpdateDto) {
    UUID uuid = validatorUtil.validateAndParseUUID(id);
    orderCommandService.updateSalesOrderStatus(uuid, orderUpdateDto);
    return ResponseEntity.status(200).body("\"OK\"");
  }

}
