package com.example.salesapi.service;

import com.example.salesapi.dto.OrderProductDto;
import com.example.salesapi.dto.OrderUpdateDto;
import com.example.salesapi.dto.ProductUpdateDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.model.enums.Status;
import com.example.salesapi.repository.OrderProductRepository;
import com.example.salesapi.repository.ReplacementProductRepository;
import com.example.salesapi.repository.SalesOrderRepository;
import com.example.salesapi.util.ValidatorUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class OrderCommandService {

  SalesOrderRepository salesOrderRepository;
  OrderProductRepository orderProductRepository;
  ReplacementProductRepository replacementProductRepository;
  OrderQueryService orderQueryService;
  ValidatorUtil validatorUtil;

  public UUID createSalesOrder() {
    SalesOrder dbSalesOrder = salesOrderRepository.save(newSalesOrder());
    return dbSalesOrder.getId();
  }

  public void addProduct(UUID id, List<Integer> productIds) {
    validatorUtil.productExists(productIds);
    validatorUtil.orderExists(id);
    validatorUtil.orderStatusIsNew(id);
    for (Integer productId : productIds) {
      if (orderProductAlreadyInOrder(id, productId)) {
        orderProductAlreadyInOrder(id, productId);
        OrderProduct orderProduct = orderQueryService.getOrderProduct(id, productId);
        updateProductQuantity(orderProduct.getId(), orderProduct.getQuantity() + 1);
      } else {
        orderProductRepository.save(addOrderProduct(id, productId));
      }
    }
  }

  public void updateProductQuantity(UUID productId, Integer quantity) {
    validatorUtil.checkProductUpdate(quantity);
    validatorUtil.orderProductExists(productId);
    OrderProduct dbOrderProduct = orderQueryService.findOrderProductById(productId);
    dbOrderProduct.setQuantity(quantity);
    orderProductRepository.save(dbOrderProduct);
  }

  public void updateSalesOrderStatus(UUID id, OrderUpdateDto orderUpdateDto) {
    validatorUtil.orderExists(id);
    validatorUtil.checkOrderUpdate(orderUpdateDto);
    SalesOrder dbSalesOrder = orderQueryService.findSalesOrderById(id);
    if (dbSalesOrder != null) {
      validatorUtil.canUpdateOrderStatus(dbSalesOrder, orderUpdateDto);
      dbSalesOrder.setStatus(Status.PAID);
      salesOrderRepository.save(dbSalesOrder);
    }
  }

  public void replaceProduct(UUID orderId, UUID orderProductId, ProductUpdateDto.ReplacedWithDto replacedWithDto) {
    validatorUtil.checkProductUpdateReplacedWith(replacedWithDto);
    validatorUtil.orderStatusIsPaid(orderId);
    validatorUtil.checkOrderProductId(orderProductId);
    OrderReplacementProduct orderReplacementProduct = new OrderReplacementProduct();
    orderReplacementProduct.setId(UUID.randomUUID());
    orderReplacementProduct.setQuantity(replacedWithDto.getQuantity());
    orderReplacementProduct.setOrderProductId(orderProductId);
    orderReplacementProduct.setProductId(replacedWithDto.getProduct_id());
    replacementProductRepository.save(orderReplacementProduct);
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

  private OrderProduct addOrderProduct(UUID id, Integer productId) {
    OrderProduct orderProduct = new OrderProduct();
    orderProduct.setId(UUID.randomUUID());
    orderProduct.setQuantity(1);
    orderProduct.setProductId(productId);
    orderProduct.setSalesOrderId(id);
    return orderProduct;
  }

  private boolean orderProductAlreadyInOrder(UUID uuid, Integer productId) {
    List<OrderProductDto> salesOrderProducts = orderQueryService.getSalesOrderProducts(uuid);
    for (OrderProductDto orderProductDto : salesOrderProducts) {
      return orderProductDto.getProduct_id().equals(productId);
    }
    return false;
  }

}
