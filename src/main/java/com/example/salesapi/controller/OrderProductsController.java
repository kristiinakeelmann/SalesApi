package com.example.salesapi.controller;

import com.example.salesapi.dto.OrderProductDto;
import com.example.salesapi.dto.ProductUpdateDto;
import com.example.salesapi.service.OrderCommandService;
import com.example.salesapi.service.OrderQueryService;
import com.example.salesapi.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class OrderProductsController {

  private final OrderCommandService orderCommandService;
  private final OrderQueryService orderQueryService;
  private final ValidatorUtil validatorUtil;

  @PostMapping(value = "/orders/{id}/products", produces = "application/json")
  private ResponseEntity<String> addProductToOrder(@PathVariable String id, @RequestBody List<Integer> productIds) {
    UUID uuid = validatorUtil.validateAndParseUUID(id);
    orderCommandService.addProduct(uuid, productIds);
    return ResponseEntity.status(201).body("\"OK\"");
  }

  @GetMapping(value = "/orders/{id}/products", produces = "application/json")
  private ResponseEntity<List<OrderProductDto>> getOrderProducts(@PathVariable String id) {
    UUID uuid = validatorUtil.validateAndParseUUID(id);
    validatorUtil.orderExists(uuid);
    return ResponseEntity.ok(orderQueryService.getSalesOrderProducts(uuid));
  }

  @PatchMapping(value = "/orders/{orderId}/products/{orderProductId}", produces = "application/json")
  private ResponseEntity<String> updateOrderProducts(@PathVariable String orderId, @PathVariable String orderProductId, @RequestBody ProductUpdateDto productUpdateDto) {
    UUID orderUuid = validatorUtil.validateAndParseUUID(orderId);
    UUID productUuid = validatorUtil.validateAndParseUUID(orderProductId);
    validatorUtil.validateProductUpdateDto(productUpdateDto);
    if (productUpdateDto.getQuantity() != null) {
      orderCommandService.updateProductQuantity(productUuid, productUpdateDto.getQuantity());
    }
    if (productUpdateDto.getReplaced_with() != null) {
      orderCommandService.replaceProduct(orderUuid, productUuid, productUpdateDto.getReplaced_with());
    }
    return ResponseEntity.status(200).body("\"OK\"");
  }

}
