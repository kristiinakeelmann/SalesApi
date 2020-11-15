package com.example.salesapi.util;

import com.example.salesapi.controller.dto.ProductDto;
import com.example.salesapi.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ProductUtil {


  public List<ProductDto> convertProductList(Iterable<Product> products) {
    List<ProductDto> productDtos = new ArrayList<>();
    for (Product product : products) {
      ProductDto productDto = convertProduct(product);
      productDtos.add(productDto);
    }
    return productDtos;
  }

  public ProductDto convertProduct(Product product) {
    ProductDto productDto = new ProductDto();
    productDto.setId(product.getId());
    productDto.setName(product.getName());
    productDto.setPrice(product.getPrice().toString());
    return productDto;
  }

}
