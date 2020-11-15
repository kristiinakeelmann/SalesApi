package com.example.salesapi.service;

import com.example.salesapi.SalesApiBadRequestException;
import com.example.salesapi.SalesApiNotFoundException;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.repository.ReplacementProductRepository;
import com.example.salesapi.util.SalesOrderUtil;
import com.example.salesapi.controller.dto.OrderDto;
import com.example.salesapi.controller.dto.OrderProductDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.Product;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.repository.OrderProductRepository;
import com.example.salesapi.repository.ProductRepository;
import com.example.salesapi.repository.SalesOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
@AllArgsConstructor
@Transactional
public class OrderQueryService {

  SalesOrderRepository salesOrderRepository;
  OrderProductRepository orderProductRepository;
  ProductRepository productRepository;
  ReplacementProductRepository replacementProductRepository;
  SalesOrderUtil salesOrderUtil;

  public OrderDto getSalesOrder(UUID id) {
    SalesOrder salesOrder = findSalesOrderById(id);
    List<OrderProductDto> orderProducts = getSalesOrderProducts(id);
    return salesOrderUtil.convertOrder(salesOrder, orderProducts);
  }

  public List<OrderProductDto> getSalesOrderProducts(UUID id) {
    List<OrderProduct> orderProducts = findSalesOrderProductBySalesOrderId(id);
    Iterable<Product> products = productRepository.findAll();
    Iterable<OrderReplacementProduct> replacementProducts = replacementProductRepository.findAll();
    return salesOrderUtil.convertOrderProduct(orderProducts, products, replacementProducts);
  }

  public void checkIfProductExists(List<Integer> productIds) {
    Iterable<Product> products = productRepository.findAll();
    for (Integer productId : productIds) {
      if (!stream(products.spliterator(), false)
        .map(Product::getId)
        .collect(Collectors.toList())
        .contains(productId)) {
        throw new SalesApiBadRequestException("\"Invalid parameters\"");
      }
    }
  }

  public void checkIfOrderExists(UUID id) {
    findSalesOrderById(id);
  }

  private SalesOrder findSalesOrderById(UUID id) {
    return salesOrderRepository.findById(id).orElseThrow(SalesApiNotFoundException::new);
  }

  private List<OrderProduct> findSalesOrderProductBySalesOrderId(UUID id) {
    return orderProductRepository.findAllBySalesOrderId(id);
  }

}
