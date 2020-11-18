package com.example.salesapi.controller;

import com.example.salesapi.dto.*;
import com.example.salesapi.model.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderControllerTest extends CommonIntegrationTest {

  @Test
  void user_can_create_order() {
    assertCreated(createOrder());
  }

  @Test
  void user_can_get_order() {
    OrderDto emptyOrder = createOrder().getBody();

    ResponseEntity<OrderDto> result = getOrder(emptyOrder);

    assertOk(result);
  }

  @Test
  void user_can_not_get_not_existing_order() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(RANDOM_UUID);

    ResponseEntity<String> result = getMissingOrder(emptyOrder);

    assertNotFound(result);
  }

  @Test
  void user_can_not_get_order_with_invalid_UUUID() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(INVALID_UUID);

    ResponseEntity<String> result = getMissingOrder(emptyOrder);

    assertNotFound(result);
  }

  @Test
  void user_can_update_order_status() {
    OrderDto emptyOrder = createOrder().getBody();
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);

    ResponseEntity<String> result = updateOrderStatus(emptyOrder, orderUpdateDto);

    assertOk(result);
    assertEquals(Status.PAID, getOrder(emptyOrder).getBody().getStatus());
  }

  @Test
  void user_can_not_update_order_status_with_invalid_status() {
    OrderDto emptyOrder = createOrder().getBody();
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(NONEXISTENT);

    ResponseEntity<String> result = updateOrderStatus(emptyOrder, orderUpdateDto);

    assertInvalidOrderStatus(result);
  }

  @Test
  void user_can_not_reverse_order_status() {
    OrderDto emptyOrder = createOrder().getBody();
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    updateOrderStatus(emptyOrder, orderUpdateDto);
    orderUpdateDto.setStatus(NEW);

    ResponseEntity<String> result = updateOrderStatus(emptyOrder, orderUpdateDto);

    assertInvalidOrderStatus(result);
  }

  @Test
  void user_can_not_update_status_for_not_existing_order() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(RANDOM_UUID);
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);

    ResponseEntity<String> result = updateMissingOrder(emptyOrder, orderUpdateDto);

    assertNotFound(result);
  }

  @Test
  void user_can_not_update_status_for_order_with_invalidUUID() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(INVALID_UUID);
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);

    ResponseEntity<String> result = updateMissingOrder(emptyOrder, orderUpdateDto);

    assertNotFound(result);
  }

  @Test
  void user_will_not_get_money_back_when_basket_total_increases_after_product_replacement() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    updateOrderStatus(orderWithProduct, orderUpdateDto);
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setQuantity(1);
    replacedWithDto.setProduct_id(999);
    productUpdateDto.setReplaced_with(replacedWithDto);
    replaceOrderProduct(orderWithProduct.getId(), orderProductDto.getId(), productUpdateDto);

    AmountDto amountDto = getOrder(orderWithProduct).getBody().getAmount();

    assertEquals(new BigDecimal("1332.92").toString(), amountDto.getDiscount());
    assertEquals(new BigDecimal("0.45").toString(), amountDto.getPaid());
    assertEquals(new BigDecimal("0.00").toString(), amountDto.getReturns());
    assertEquals(new BigDecimal("0.45").toString(), amountDto.getTotal());
  }

  @Test
  void user_will_get_money_back_when_basket_total_decreases_after_product_replacement() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(999));
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    updateOrderStatus(orderWithProduct, orderUpdateDto);
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setQuantity(100);
    replacedWithDto.setProduct_id(123);
    productUpdateDto.setReplaced_with(replacedWithDto);
    replaceOrderProduct(orderWithProduct.getId(), orderProductDto.getId(), productUpdateDto);

    AmountDto amountDto = getOrder(orderWithProduct).getBody().getAmount();

    assertEquals(new BigDecimal("0.00").toString(), amountDto.getDiscount());
    assertEquals(new BigDecimal("1333.37").toString(), amountDto.getPaid());
    assertEquals(new BigDecimal("1288.37").toString(), amountDto.getReturns());
    assertEquals(new BigDecimal("45.00").toString(), amountDto.getTotal());
  }

}
