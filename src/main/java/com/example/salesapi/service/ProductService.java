package com.example.salesapi.service;

import com.example.salesapi.util.ProductUtil;
import com.example.salesapi.model.Product;
import com.example.salesapi.repository.ProductRepository;
import com.example.salesapi.controller.dto.ProductDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductUtil productUtil;
  public List<ProductDto> getProducts() {
    Iterable<Product> allProducts = productRepository.findAll();
    return productUtil.convertProductList(allProducts);
  }

}
