package com.example.salesapi.util;

import com.example.salesapi.controller.dto.AmountDto;
import com.example.salesapi.controller.dto.OrderDto;
import com.example.salesapi.controller.dto.OrderProductDto;
import com.example.salesapi.model.OrderProduct;
import com.example.salesapi.model.OrderReplacementProduct;
import com.example.salesapi.model.Product;
import com.example.salesapi.model.SalesOrder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class SalesOrderUtil {
  public OrderDto convertOrder(SalesOrder salesOrder, List<OrderProductDto> orderProductDtos) {
    OrderDto orderDto = new OrderDto();
    orderDto.setId(salesOrder.getId().toString());
    orderDto.setStatus(salesOrder.getStatus());
    orderDto.setProducts(orderProductDtos);
    orderDto.setAmount(convertAmount(salesOrder));
    return orderDto;
  }

  public AmountDto convertAmount(SalesOrder salesOrder) {
    AmountDto amountDto = new AmountDto();
    amountDto.setDiscount(salesOrder.getDiscount().toString());
    amountDto.setPaid(salesOrder.getPaid().toString());
    amountDto.setReturns(salesOrder.getReturns().toString());
    amountDto.setTotal(salesOrder.getTotal().toString());
    return amountDto;
  }

  public List<OrderProductDto> convertOrderProduct(Iterable<OrderProduct> orderProducts, Iterable<Product> products,
                                                   Iterable<OrderReplacementProduct> replacementProducts) {
    List<OrderProductDto> orderProductDtos = new ArrayList<>();
    for (OrderProduct orderProduct : orderProducts) {
      OrderProductDto orderProductDto = new OrderProductDto();
      orderProductDto.setId(orderProduct.getId().toString());
      orderProductDto.setQuantity(orderProduct.getQuantity());
      for (Product product : products) {
        if (orderProduct.getProductId() == product.getId()) {
          orderProductDto.setName(product.getName());
          orderProductDto.setPrice(product.getPrice());
          orderProductDto.setProduct_id(product.getId());
        }
        for (OrderReplacementProduct orderReplacementProduct : replacementProducts) {
          if (orderProduct.getId() == orderReplacementProduct.getOrderProductId()) {
            OrderProductDto replacementOrderProduct = new OrderProductDto();
            replacementOrderProduct.setId(orderReplacementProduct.getId().toString());
            replacementOrderProduct.setName(product.getName());
            replacementOrderProduct.setPrice(product.getPrice());
            replacementOrderProduct.setProduct_id(product.getId());
            replacementOrderProduct.setQuantity(orderReplacementProduct.getQuantity());
            orderProductDto.setReplaced_with(replacementOrderProduct);
          }
        }
      }
      orderProductDtos.add(orderProductDto);
    }
    return orderProductDtos;
  }

}
