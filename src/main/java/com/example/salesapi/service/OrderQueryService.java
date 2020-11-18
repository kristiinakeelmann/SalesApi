package com.example.salesapi.service;

import com.example.salesapi.dto.OrderDto;
import com.example.salesapi.dto.OrderProductDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.repository.OrderProductRepository;
import com.example.salesapi.repository.SalesOrderRepository;
import com.example.salesapi.util.SalesOrderUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class OrderQueryService {

  SalesOrderRepository salesOrderRepository;
  OrderProductRepository orderProductRepository;
  SalesOrderUtil salesOrderUtil;


  public OrderDto getSalesOrder(UUID id) {
    SalesOrder salesOrder = findSalesOrderById(id);
    List<OrderProductDto> orderProducts = getSalesOrderProducts(id);
    if (salesOrder != null) {
      return salesOrderUtil.convertOrder(salesOrder, orderProducts);
    } else return null;
  }

  public List<OrderProductDto> getSalesOrderProducts(UUID id) {
    List<OrderProduct> orderProducts = findSalesOrderProductBySalesOrderId(id);
    return salesOrderUtil.convertOrderProduct(orderProducts);
  }

  public OrderProduct getOrderProduct(UUID orderId, Integer productId) {
    return orderProductRepository.findBySalesOrderIdAndProductId(orderId, productId);
  }

  public OrderProduct findOrderProductById(UUID id) {
    return orderProductRepository.findById(id).orElse(null);
  }

  public SalesOrder findSalesOrderById(UUID id) {
    return salesOrderRepository.findById(id).orElse(null);
  }

  private List<OrderProduct> findSalesOrderProductBySalesOrderId(UUID id) {
    return orderProductRepository.findAllBySalesOrderId(id);
  }
}
