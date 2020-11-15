package com.example.salesapi.controller;

import com.example.salesapi.controller.dto.ProductDto;
import com.example.salesapi.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ProductsController {

  private final ProductService productService;

  @GetMapping("/products")
  private List<ProductDto> getProducts() {
    return productService.getProducts();
  }

}
