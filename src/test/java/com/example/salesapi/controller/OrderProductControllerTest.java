package com.example.salesapi.controller;

import com.example.salesapi.dto.OrderDto;
import com.example.salesapi.dto.OrderProductDto;
import com.example.salesapi.dto.OrderUpdateDto;
import com.example.salesapi.dto.ProductUpdateDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderProductControllerTest extends CommonIntegrationTest {

  @Test
  void user_can_add_one_product_to_order() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productId = Collections.singletonList(123);

    ResponseEntity<String> result = addProductToOrder(emptyOrder, productId);

    assertCreated(result);
  }

  @Test
  void user_can_add_several_products_to_order() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productIds = List.of(123, 999);

    ResponseEntity<String> result = addProductToOrder(emptyOrder, productIds);

    assertCreated(result);
  }

  @Test
  void user_can_add_same_product_several_times() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productIdFirstTime = Collections.singletonList(123);
    addProductToOrder(emptyOrder, productIdFirstTime);
    List<Integer> productIdSecondTime = Collections.singletonList(123);
    addProductToOrder(emptyOrder, productIdSecondTime);

    ResponseEntity<List<OrderProductDto>> result = getOrderProducts(emptyOrder);

    List<OrderProductDto> orderProductDtos = assertOk(result);
    assertEquals(1, orderProductDtos.size());
  }

  @Test
  void user_can_not_add_missing_product_to_order() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productIds = Collections.singletonList(-1);

    ResponseEntity<String> result = addProductToOrder(emptyOrder, productIds);

    assertInvalidParameters(result);
  }

  @Test
  void user_can_not_add_products_to_order_if_one_product_is_missing() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productIds = List.of(123, -1);

    ResponseEntity<String> result = addProductToOrder(emptyOrder, productIds);

    assertInvalidParameters(result);
  }

  @Test
  void user_can_not_add_products_to_order_when_order_is_paid() {
    OrderDto emptyOrder = createOrder().getBody();
    List<Integer> productId = Collections.singletonList(123);
    addProductToOrder(emptyOrder, productId);
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    updateOrderStatus(emptyOrder, orderUpdateDto);

    ResponseEntity<String> result = addProductToOrder(emptyOrder, productId);

    assertInvalidParameters(result);
  }

  @Test
  void user_can_not_add_products_to_not_existing_order() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(RANDOM_UUID);
    List<Integer> productIds = Collections.singletonList(123);

    ResponseEntity<String> result = addProductToMissingOrder(emptyOrder, productIds);

    assertNotFound(result);
  }

  @Test
  void user_can_not_add_products_to_order_with_invalidUUID() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(INVALID_UUID);
    List<Integer> productIds = Collections.singletonList(123);

    ResponseEntity<String> result = addProductToMissingOrder(emptyOrder, productIds);

    assertNotFound(result);
  }

  @Test
  void user_can_get_order_products() {
    OrderDto emptyOrder = createOrder().getBody();

    ResponseEntity<List<OrderProductDto>> result = getOrderProducts(emptyOrder);

    assertOk(result);
  }

  @Test
  void user_can_not_get_not_existing_order_products() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(RANDOM_UUID);

    ResponseEntity<String> result = getMissingOrderProducts(emptyOrder);

    assertNotFound(result);
  }

  @Test
  void user_can_not_get_order_products_with_invalidUUID() {
    OrderDto emptyOrder = createOrder().getBody();
    emptyOrder.setId(INVALID_UUID);

    ResponseEntity<String> result = getMissingOrderProducts(emptyOrder);

    assertNotFound(result);
  }

  @Test
  void user_can_update_order_product_quantity() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setQuantity(5);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();

    ResponseEntity<String> result = updateOrderProducts(orderWithProduct, orderWithProduct.getProducts().get(0), productUpdateDto);

    assertOk(result);
    OrderDto updatedOrder = getOrder(orderWithProduct).getBody();
    assertEquals(5, updatedOrder.getProducts().get(0).getQuantity());
  }

  @Test
  void user_can_not_update_order_product_with_invalid_quantity() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    OrderDto dbOrderDto = getOrder(emptyOrder).getBody();
    OrderProductDto orderProductDto = dbOrderDto.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    productUpdateDto.setQuantity(-5);

    ResponseEntity<String> result = updateOrderProducts(dbOrderDto, orderProductDto, productUpdateDto);

    assertInvalidParameters(result);
  }

  @Test
  void user_can_replace_product_if_order_status_is_paid() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    updateOrderStatus(orderWithProduct, orderUpdateDto);
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setQuantity(2);
    replacedWithDto.setProduct_id(999);
    productUpdateDto.setReplaced_with(replacedWithDto);

    ResponseEntity<String> result = replaceOrderProduct(orderWithProduct.getId(), orderProductDto.getId(), productUpdateDto);

    assertOk(result);
    OrderProductDto replacementProduct = getOrderProducts(orderWithProduct).getBody().get(0).getReplaced_with();
    assertNotNull(replacementProduct);
    assertEquals("75\" OLED TV", replacementProduct.getName());
    assertEquals("1333.37", replacementProduct.getPrice());
    assertEquals(999, replacementProduct.getProduct_id());
    assertEquals(2, replacementProduct.getQuantity());
  }

  @Test
  void user_can_not_replace_product_if_order_status_is_not_paid() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setProduct_id(123);
    replacedWithDto.setQuantity(99);
    productUpdateDto.setReplaced_with(replacedWithDto);

    ResponseEntity<String> result = replaceOrderProductForNewOrder(orderWithProduct, orderProductDto, productUpdateDto);

    assertInvalidParameters(result);
  }

  @Test
  void user_can_not_replace_already_replaced_product() {
    OrderDto emptyOrder = createOrder().getBody();
    addProductToOrder(emptyOrder, Collections.singletonList(123));
    OrderUpdateDto orderUpdateDto = new OrderUpdateDto();
    orderUpdateDto.setStatus(PAID);
    OrderDto orderWithProduct = getOrder(emptyOrder).getBody();
    updateOrderStatus(orderWithProduct, orderUpdateDto);
    OrderProductDto orderProductDto = orderWithProduct.getProducts().get(0);
    ProductUpdateDto productUpdateDto = new ProductUpdateDto();
    ProductUpdateDto.ReplacedWithDto replacedWithDto = new ProductUpdateDto.ReplacedWithDto();
    replacedWithDto.setQuantity(88);
    replacedWithDto.setProduct_id(123);
    productUpdateDto.setReplaced_with(replacedWithDto);
    replaceOrderProduct(orderWithProduct.getId(), orderProductDto.getId(), productUpdateDto);
    OrderDto orderDtoWithReplacedProduct = getOrder(orderWithProduct).getBody();
    String updatedOrderProductDto = orderDtoWithReplacedProduct.getProducts().get(0).getReplaced_with().getId();

    ResponseEntity<String> result = replaceOrderProduct(orderWithProduct.getId(), updatedOrderProductDto, productUpdateDto);

    assertNotFound(result);
  }

}
