package com.example.salesapi.service;

import com.example.salesapi.dto.ProductDto;
import com.example.salesapi.model.Product;
import com.example.salesapi.repository.ProductRepository;
import com.example.salesapi.util.ProductUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ProductQueryService {

  private final ProductRepository productRepository;
  private final ProductUtil productUtil;

  public List<ProductDto> getProducts() {
    return productUtil.convertProducts(productRepository.findAll());
  }

  public ProductDto findProductById(Integer id) {
    Product product = productRepository.findById(id).orElse(null);
    if (product != null) {
      return productUtil.convertProduct(product);
    }
    return null;
  }

}
