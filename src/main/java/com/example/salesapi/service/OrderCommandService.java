package com.example.salesapi.service;

import com.example.salesapi.SalesApiBadRequestException;
import com.example.salesapi.controller.dto.OrderUpdateDto;
import com.example.salesapi.controller.dto.ProductUpdateDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.model.enums.Status;
import com.example.salesapi.repository.OrderProductRepository;
import com.example.salesapi.repository.ReplacementProductRepository;
import com.example.salesapi.repository.SalesOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class OrderCommandService {

  SalesOrderRepository salesOrderRepository;
  OrderProductRepository orderProductRepository;
  ReplacementProductRepository replacementProductRepository;

  public UUID createSalesOrder() {
    SalesOrder dbSalesOrder = salesOrderRepository.save(newSalesOrder());
    return dbSalesOrder.getId();
  }

  public void addProduct(UUID id, List<Integer> productId) {
    if (findSalesOrderById(id) != null) {
      orderProductRepository.saveAll(addOrderProduct(id, productId));
    }
  }

  public void updateSalesOrderStatus(UUID id, OrderUpdateDto orderUpdateDto) {
    SalesOrder dbSalesOrder = findSalesOrderById(id);
    if (dbSalesOrder.getStatus() == Status.NEW && orderUpdateDto.getStatus() == Status.PAID) {
      dbSalesOrder.setStatus(Status.PAID);
      salesOrderRepository.save(dbSalesOrder);
    } else throw new SalesApiBadRequestException("\"Invalid order status\"");
  }

  public void updateProductQuantity(UUID orderId, UUID productId, Integer quantity) {
    OrderProduct dbOrderProduct = findOrderProductById(productId);
      dbOrderProduct.setQuantity(quantity);
      orderProductRepository.save(dbOrderProduct);
  }

  public void replaceProduct(UUID orderId, UUID productId, ProductUpdateDto.ReplacedWithDto replacedWithDto) {
    if (findSalesOrderById(orderId).getStatus() == Status.PAID) {
      OrderReplacementProduct orderReplacementProduct = new OrderReplacementProduct();
      orderReplacementProduct.setId(UUID.randomUUID());
      orderReplacementProduct.setQuantity(replacedWithDto.getQuantity());
      orderReplacementProduct.setOrderProductId(productId);
      orderReplacementProduct.setProductId(replacedWithDto.getProduct_id());
      replacementProductRepository.save(orderReplacementProduct);
    } else throw new SalesApiBadRequestException("\"Invalid order status\"");
  }

  private SalesOrder newSalesOrder() {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setId(UUID.randomUUID());
    salesOrder.setStatus(Status.NEW);
    salesOrder.setDiscount(new BigDecimal("0.00"));
    salesOrder.setPaid(new BigDecimal("0.00"));
    salesOrder.setReturns(new BigDecimal("0.00"));
    salesOrder.setTotal(new BigDecimal("0.00"));
    return salesOrder;
  }

  private List<OrderProduct> addOrderProduct(UUID id, List<Integer> productIds) {
    List<OrderProduct> orderProducts = new ArrayList<>();
    for (Integer productId : productIds) {
      OrderProduct orderProduct = new OrderProduct();
      orderProduct.setId(UUID.randomUUID());
      orderProduct.setQuantity(1);
      orderProduct.setProductId(productId);
      orderProduct.setSalesOrderId(id);
      orderProducts.add(orderProduct);
    }
    return orderProducts;
  }

  private SalesOrder findSalesOrderById(UUID id) {
    return salesOrderRepository.findById(id).orElseThrow(() ->
      new SalesApiBadRequestException(String.format("Order missing with id %d", id)));
  }

  private OrderProduct findOrderProductById(UUID id) {
    return orderProductRepository.findById(id).orElseThrow(() ->
      new SalesApiBadRequestException(String.format("Order product missing with id %d", id)));
  }

}
