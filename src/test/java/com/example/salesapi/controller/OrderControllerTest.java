package com.example.salesapi.controller;

import com.example.salesapi.controller.dto.OrderDto;
import com.example.salesapi.controller.dto.OrderProductDto;
import com.example.salesapi.controller.dto.OrderUpdateDto;
import com.example.salesapi.controller.dto.ProductUpdateDto;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.model.enums.Status;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderControllerTest extends CommonIntegrationTest {

  @Test
  void user_can_create_order() {
    assertOk(createOrder());
  }

  @Test
  void user_can_add_product_to_order() {
    OrderDto emptyOrder = assertOk(createOrder());
    List<Integer> productIds = Collections.singletonList(123);
    assertOk(addProductToOrder(emptyOrder, productIds));
  }

  @Test
  void user_can_not_add_missing_product_to_order() {
    OrderDto emptyOrder = assertOk(createOrder());
    List<Integer> productIds = Collections.singletonList(-1);
    assertInvalidParameters(addProductToOrder(emptyOrder, productIds));
  }

  @Test
  void user_can_not_add_products_to_not_existing_order() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(RANDOM_UUID);
    List<Integer> productIds = Collections.singletonList(123);
    assertNotFound(addProductToMissingOrder(emptyOrder, productIds));
  }

  @Test
  void user_can_not_add_products_to_order_with_invalidUUID() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(INVALID_UUID);
    List<Integer> productIds = Collections.singletonList(123);
    assertNotFound(addProductToMissingOrder(emptyOrder, productIds));
  }

  @Test
  void user_can_get_order() {
    OrderDto emptyOrder = assertOk(createOrder());
    assertOk(getOrder(emptyOrder));
  }

  @Test
  void user_can_not_get_not_existing_order() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(RANDOM_UUID);
    assertNotFound(getMissingOrder(emptyOrder));
  }

  @Test
  void user_can_not_get_order_with_invalid_UUUID() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(INVALID_UUID);
    assertNotFound(getMissingOrder(emptyOrder));
  }

  @Test
  void user_can_get_order_products() {
    OrderDto emptyOrder = assertOk(createOrder());
    assertOk(getOrderProducts(emptyOrder));
  }

  @Test
  void user_can_not_get_not_existing_order_products() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(RANDOM_UUID);
    assertNotFound(getMissingOrderProducts(emptyOrder));
  }

  @Test
  void user_can_not_get_order_products_with_invalidUUID() {
    OrderDto emptyOrder = assertOk(createOrder());
    emptyOrder.setId(INVALID_UUID);
    assertNotFound(getMissingOrderProducts(emptyOrder));
  }

  @Test
  void user_can_update_order_status() {
    OrderDto emptyOrder = assertOk(createOrder());
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(Status.PAID);
    assertOk(updateOrderStatus(emptyOrder, orderUpdateDto));
    assertEquals(getOrder(emptyOrder).getBody().getStatus(), Status.PAID);
  }

  @Test
  void user_can_not_reverse_order_status() {
    OrderDto emptyOrder = assertOk(createOrder());
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(Status.PAID);
    assertOk(updateOrderStatus(emptyOrder, orderUpdateDto));
    orderUpdateDto.setStatus(Status.NEW);
    assertInvalidParameters(updateOrderStatus(emptyOrder, orderUpdateDto));
  }

  @Test
  void user_can_not_update_status_for_not_existing_order() {
    OrderDto orderDto = assertOk(createOrder());
    orderDto.setId(RANDOM_UUID);
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(Status.PAID);
    assertNotFound(updateMissingOrder(orderDto, orderUpdateDto));
  }

  @Test
  void user_can_not_update_status_for_order_with_invalidUUID() {
    OrderDto orderDto = assertOk(createOrder());
    orderDto.setId(INVALID_UUID);
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(Status.PAID);
    assertNotFound(updateMissingOrder(orderDto, orderUpdateDto));
  }

  @Test
  void user_can_update_order_product_quantity() {
    OrderDto emptyOrder = assertOk(createOrder());
    assertOk(addProductToOrder(emptyOrder, Collections.singletonList(123)));
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setQuantity(5);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    assertOk(updateOrderProducts(orderWithProduct, orderWithProduct.getProducts().get(0), productUpdateDto));
    OrderDto updatedOrder = getOrder(orderWithProduct).getBody();
    assertEquals(5, updatedOrder.getProducts().get(0).getQuantity());
  }

  @Test
  void user_can_not_update_order_product_with_invalid_quantity() {
    OrderDto orderDto = assertOk(createOrder());
    assertOk(addProductToOrder(orderDto, Collections.singletonList(123)));
    OrderDto dbOrderDto = getOrder(orderDto).getBody();
    OrderProductDto orderProductDto = dbOrderDto.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setQuantity(-5);
    assertInvalidParameters(updateOrderProducts(dbOrderDto, orderProductDto, productUpdateDto));
  }

  @Test
  void user_can_replace_product_if_order_status_is_paid() {
    OrderDto emptyOrder = assertOk(createOrder());
    assertOk(addProductToOrder(emptyOrder, Collections.singletonList(123)));
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(Status.PAID);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    assertOk(updateOrderStatus(orderWithProduct, orderUpdateDto));
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    OrderReplacementProduct orderReplacementProduct = new OrderReplacementProduct();
    orderReplacementProduct.setId(UUID.randomUUID());
    orderReplacementProduct.setProductId(123);
    orderReplacementProduct.setQuantity(8);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    productUpdateDto.setReplaced_with(replacedWithDto);
    assertOk(replaceOrderProduct(orderWithProduct, orderProductDto, productUpdateDto));
  }

  @Test
  void user_can_not_replace_product_if_order_status_is_not_paid() {
    OrderDto emptyOrder = assertOk(createOrder());
    assertOk(addProductToOrder(emptyOrder, Collections.singletonList(123)));
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setProduct_id(123);
    replacedWithDto.setQuantity(99);
    productUpdateDto.setReplaced_with(replacedWithDto);
    OrderReplacementProduct orderReplacementProduct = new OrderReplacementProduct();
    orderReplacementProduct.setId(UUID.randomUUID());
    orderReplacementProduct.setProductId(999);
    orderReplacementProduct.setQuantity(8);
    assertInvalidParameters(replaceOrderProductForNewOrder(orderWithProduct, orderProductDto, productUpdateDto));
  }

}
