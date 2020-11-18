package com.example.salesapi.controller;

import com.example.salesapi.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommonIntegrationTest {

  @Resource
  TestRestTemplate testRestTemplate;

  public static final String PRODUCTS_URL = "/products";
  public static final String ORDER_URL = "/orders";
  public static final String RANDOM_UUID = UUID.randomUUID().toString();
  public static final String INVALID_UUID = UUID.randomUUID() + "123";
  public static final String PAID = "PAID";
  public static final String NEW = "NEW";
  public static final String NONEXISTENT = "NONEXISTENT";
  private static final ParameterizedTypeReference<List<ProductDto>> LIST_OF_PRODUCTS = new ParameterizedTypeReference<>() {
  };
  private static final ParameterizedTypeReference<OrderDto> ORDER = new ParameterizedTypeReference<>() {
  };
  private static final ParameterizedTypeReference<List<OrderProductDto>> LIST_OF_ORDER_PRODUCTS = new ParameterizedTypeReference<>() {
  };

  @BeforeEach
  void setUp() {
    testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  public ResponseEntity<List<ProductDto>> getProducts() {
    return testRestTemplate.exchange(PRODUCTS_URL, HttpMethod.GET, null, LIST_OF_PRODUCTS);
  }

  public ResponseEntity<OrderDto> createOrder() {
    return testRestTemplate.exchange(ORDER_URL, HttpMethod.POST, null, ORDER);
  }

  public ResponseEntity<OrderDto> getOrder(OrderDto orderDto) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId(), HttpMethod.GET, null, ORDER);
  }

  public ResponseEntity<String> getMissingOrder(OrderDto orderDto) {
    ResponseEntity<String> exceptionResponseEntity = testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId(), HttpMethod.GET, null, String.class);
    assertEquals(HttpStatus.NOT_FOUND, exceptionResponseEntity.getStatusCode());
    return exceptionResponseEntity;
  }

  public ResponseEntity<String> updateOrderStatus(OrderDto orderDto, OrderUpdateDto orderUpdateDto) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId(), HttpMethod.PATCH, new HttpEntity<>(orderUpdateDto), String.class);
  }

  public ResponseEntity<String> updateMissingOrder(OrderDto orderDto, OrderUpdateDto orderUpdateDto) {
    ResponseEntity<String> exceptionResponseEntity = testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId(), HttpMethod.PATCH, new HttpEntity<>(orderUpdateDto), String.class);
    assertEquals(HttpStatus.NOT_FOUND, exceptionResponseEntity.getStatusCode());
    return exceptionResponseEntity;
  }

  public ResponseEntity<List<OrderProductDto>> getOrderProducts(OrderDto orderDto) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products", HttpMethod.GET, null, LIST_OF_ORDER_PRODUCTS);
  }

  public ResponseEntity<String> getMissingOrderProducts(OrderDto orderDto) {
    ResponseEntity<String> exceptionResponseEntity = testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products", HttpMethod.GET, null, String.class);
    assertEquals(HttpStatus.NOT_FOUND, exceptionResponseEntity.getStatusCode());
    return exceptionResponseEntity;
  }

  public ResponseEntity<String> addProductToOrder(OrderDto orderDto, List<Integer> productIds) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products", HttpMethod.POST, new HttpEntity<Object>(productIds), String.class);
  }

  public ResponseEntity<String> addProductToMissingOrder(OrderDto orderDto, List<Integer> productIds) {
    ResponseEntity<String> exceptionResponseEntity = testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products", HttpMethod.POST, new HttpEntity<Object>(productIds), String.class);
    assertEquals(HttpStatus.NOT_FOUND, exceptionResponseEntity.getStatusCode());
    return exceptionResponseEntity;
  }

  public ResponseEntity<String> updateOrderProducts(OrderDto orderDto, OrderProductDto orderProductDto, ProductUpdateDto productUpdateDto) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products/" + orderProductDto.getId(), HttpMethod.PATCH, new HttpEntity<>(productUpdateDto), String.class);
  }

  public ResponseEntity<String> replaceOrderProduct(String orderId, String orderProductId, ProductUpdateDto productUpdateDto) {
    return testRestTemplate.exchange(ORDER_URL + "/" + orderId+ "/products/" + orderProductId, HttpMethod.PATCH, new HttpEntity<>(productUpdateDto), String.class);
  }

  public ResponseEntity<String> replaceOrderProductForNewOrder(OrderDto orderDto, OrderProductDto orderProductDto, ProductUpdateDto productUpdateDto) {
    ResponseEntity<String> exceptionResponseEntity = testRestTemplate.exchange(ORDER_URL + "/" + orderDto.getId() + "/products/" + orderProductDto.getId(), HttpMethod.PATCH, new HttpEntity<>(productUpdateDto), String.class);
    assertEquals(HttpStatus.BAD_REQUEST, exceptionResponseEntity.getStatusCode());
    return exceptionResponseEntity;
  }

  public <T> T assertOk(ResponseEntity<T> responseEntity) {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    return responseEntity.getBody();
  }

  public <T> T assertCreated(ResponseEntity<T> responseEntity) {
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    return responseEntity.getBody();
  }

  public <T> T assertNotFound(ResponseEntity<T> responseEntity) {
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertEquals("\"Not found\"", responseEntity.getBody());
    return responseEntity.getBody();
  }

  public <T> T assertInvalidParameters(ResponseEntity<T> responseEntity) {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("\"Invalid parameters\"", responseEntity.getBody());
    return responseEntity.getBody();
  }

  public <T> T assertInvalidOrderStatus(ResponseEntity<T> responseEntity) {
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertEquals("\"Invalid order status\"", responseEntity.getBody());
    return responseEntity.getBody();
  }

}
