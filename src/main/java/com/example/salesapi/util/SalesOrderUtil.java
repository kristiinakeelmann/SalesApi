package com.example.salesapi.util;

import com.example.salesapi.dto.AmountDto;
import com.example.salesapi.dto.OrderDto;
import com.example.salesapi.dto.OrderProductDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.model.Product;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.model.enums.Status;
import com.example.salesapi.repository.ProductRepository;
import com.example.salesapi.repository.ReplacementProductRepository;
import com.example.salesapi.service.CalculationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SalesOrderUtil {

  ProductRepository productRepository;
  ReplacementProductRepository replacementProductRepository;
  CalculationService calculationService;

  public OrderDto convertOrder(SalesOrder salesOrder, List<OrderProductDto> orderProductDtos) {
    OrderDto orderDto = new OrderDto();
    orderDto.setId(salesOrder.getId().toString());
    orderDto.setStatus(salesOrder.getStatus());
    orderDto.setProducts(orderProductDtos);
    orderDto.setAmount(convertAmountDto(salesOrder, orderProductDtos));
    return orderDto;
  }

  public AmountDto convertAmountDto(SalesOrder salesOrder, List<OrderProductDto> orderProductDtos) {
    BigDecimal paidSum;
    BigDecimal finalSum;
    BigDecimal discount;
    BigDecimal returns;
    BigDecimal total;
    if (salesOrder.getStatus() == Status.PAID) {
      paidSum = calculationService.originalSalesOrderSum(orderProductDtos);
      finalSum = calculationService.finalSalesOrderSum(orderProductDtos);
      discount = calculationService.salesOrderDiscountSum(paidSum, finalSum);
      returns = calculationService.salesOrderReturnsSum(paidSum, finalSum);
      total = paidSum.subtract(returns);
    } else {
      paidSum = new BigDecimal("0.00");
      discount = new BigDecimal("0.00");
      returns = new BigDecimal("0.00");
      total = calculationService.originalSalesOrderSum(orderProductDtos);
    }
    AmountDto amountDto = new AmountDto();
    amountDto.setDiscount(discount.toString());
    amountDto.setPaid(paidSum.toString());
    amountDto.setReturns(returns.toString());
    amountDto.setTotal(total.toString());
    return amountDto;
  }

  public List<OrderProductDto> convertOrderProduct(Iterable<OrderProduct> orderProducts) {
    List<OrderProductDto> orderProductDtos = new ArrayList<>();
    for (OrderProduct orderProduct : orderProducts) {
      OrderProductDto orderProductDto = getOrderProduct(orderProduct);
      orderProductDtos.add(orderProductDto);
    }
    return orderProductDtos;
  }

  private OrderProductDto getOrderProduct(OrderProduct orderProduct) {
    OrderProductDto orderProductDto = new OrderProductDto();
    orderProductDto.setId(orderProduct.getId().toString());
    orderProductDto.setQuantity(orderProduct.getQuantity());
    Product product = productRepository.findById(orderProduct.getProductId()).orElse(null);
    orderProductDto.setName(product.getName());
    orderProductDto.setPrice(product.getPrice().toString());
    orderProductDto.setProduct_id(product.getId());
    OrderReplacementProduct orderReplacementProduct = replacementProductRepository.findByOrderProductId(orderProduct.getId()).orElse(null);
    if (orderReplacementProduct != null) {
      getOrderReplacementProduct(orderProductDto, orderReplacementProduct);
    }
    return orderProductDto;
  }

  private void getOrderReplacementProduct(OrderProductDto orderProductDto, OrderReplacementProduct orderReplacementProduct) {
    OrderProductDto orderReplacementProductDto = new OrderProductDto();
    orderReplacementProductDto.setId(orderReplacementProduct.getId().toString());
    orderReplacementProductDto.setQuantity(orderReplacementProduct.getQuantity());
    Product replacementProduct = productRepository.findById(orderReplacementProduct.getProductId()).orElse(null);
    orderReplacementProductDto.setName(replacementProduct.getName());
    orderReplacementProductDto.setPrice(replacementProduct.getPrice().toString());
    orderReplacementProductDto.setProduct_id(replacementProduct.getId());
    orderProductDto.setReplaced_with(orderReplacementProductDto);
  }

}
