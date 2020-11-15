package com.example.salesapi.controller;

import com.example.salesapi.SalesApiBadRequestException;
import com.example.salesapi.SalesApiNotFoundException;
import com.example.salesapi.controller.dto.OrderDto;
import com.example.salesapi.controller.dto.OrderProductDto;
import com.example.salesapi.controller.dto.OrderUpdateDto;
import com.example.salesapi.controller.dto.ProductUpdateDto;
import com.example.salesapi.service.OrderCommandService;
import com.example.salesapi.service.OrderQueryService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.salesapi.controller.ControllerUtil.SUCCESS_RESPONSE;

@RestController
@AllArgsConstructor
public class OrdersController {

  private final OrderCommandService orderCommandService;
  private final OrderQueryService orderQueryService;

  @PostMapping("/orders")
  private OrderDto createOrder() {
    UUID salesOrderId = orderCommandService.createSalesOrder();
    return orderQueryService.getSalesOrder(salesOrderId);
  }

  @PostMapping("/orders/{id}/products")
  private String addProductToOrder(@PathVariable String id, @RequestBody List<Integer> productIds) {
    orderQueryService.checkIfProductExists(productIds);
    try {
      UUID uuid = UUID.fromString(id);
      orderCommandService.addProduct(uuid, productIds);
      return SUCCESS_RESPONSE;
    } catch (IllegalArgumentException e) {
      throw new SalesApiNotFoundException();
    }
  }

  @GetMapping("/orders/{id}")
  private OrderDto getOrder(@PathVariable String id) {
    try {
      UUID uuid = UUID.fromString(id);
      return orderQueryService.getSalesOrder(uuid);
    } catch (IllegalArgumentException e) {
      throw new SalesApiNotFoundException();
    }
  }

  @GetMapping("/orders/{id}/products")
  private List<OrderProductDto> getOrderProducts(@PathVariable String id) {
    try {
      UUID uuid = UUID.fromString(id);
      orderQueryService.checkIfOrderExists(uuid);
      return orderQueryService.getSalesOrderProducts(uuid);
    } catch (IllegalArgumentException e) {
      throw new SalesApiNotFoundException();
    }
  }

  @PatchMapping("/orders/{id}")
  private String updateOrder(@PathVariable String id, @RequestBody OrderUpdateDto orderUpdateDto) {
    try {
      UUID uuid = UUID.fromString(id);
      orderCommandService.updateSalesOrderStatus(uuid, orderUpdateDto);
      return SUCCESS_RESPONSE;
    } catch (IllegalArgumentException e) {
      throw new SalesApiNotFoundException();
    }
  }

  @PatchMapping("/orders/{orderId}/products/{productId}")
  private String updateOrderProducts(@PathVariable UUID orderId, @PathVariable UUID productId, @RequestBody ProductUpdateDto productUpdateDto) {
    if (productUpdateDto.getQuantity() != null) {
      if (productUpdateDto.getQuantity() < 0) {
        throw new SalesApiBadRequestException("\"Invalid parameters\"");
      }
      orderCommandService.updateProductQuantity(orderId, productId, productUpdateDto.getQuantity());
    }
    if (productUpdateDto.getReplaced_with() != null) {
      orderCommandService.replaceProduct(orderId, productId, productUpdateDto.getReplaced_with());
    }
    return SUCCESS_RESPONSE;
  }

}
