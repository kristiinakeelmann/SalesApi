package com.example.salesapi.controller;

import com.example.salesapi.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductControllerTest extends CommonIntegrationTest {

  @Test
  void user_can_get_product_list() {
    ResponseEntity<List<ProductDto>> result = getProducts();

    List<ProductDto> products = assertOk(result);
    assertTrue(products.size() > 0);
  }

}
