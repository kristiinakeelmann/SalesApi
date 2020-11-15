package com.example.salesapi.controller;

import com.example.salesapi.controller.dto.ProductDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProductControllerTest extends CommonIntegrationTest {

  @Test
  void user_can_get_product_list() {
    List<ProductDto> products = assertOk(getProducts());
    assertTrue(products.size() > 0);
  }

}
