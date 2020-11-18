package com.example.salesapi.controller;

import com.example.salesapi.dto.ProductDto;
import com.example.salesapi.service.ProductQueryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class ProductsController {

  private final ProductQueryService productQueryService;

  @GetMapping(value="/products", produces="application/json")
  private ResponseEntity<List<ProductDto>> getProducts() {
    return ResponseEntity.ok(productQueryService.getProducts());
  }

}
