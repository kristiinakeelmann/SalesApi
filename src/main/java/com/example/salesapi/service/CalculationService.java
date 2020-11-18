package com.example.salesapi.service;

import com.example.salesapi.dto.OrderProductDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CalculationService {

  public BigDecimal originalSalesOrderSum(List<OrderProductDto> orderProductDtos) {
    BigDecimal paid = new BigDecimal("0.00");
    for (OrderProductDto orderProductDto : orderProductDtos) {
      BigDecimal orderProductSum = new BigDecimal(orderProductDto.getPrice()).multiply(new BigDecimal(orderProductDto.getQuantity()));
      paid = paid.add(orderProductSum);
    }
    return paid;
  }

  public BigDecimal finalSalesOrderSum(List<OrderProductDto> orderProductDtos) {
    BigDecimal total = new BigDecimal("0.00");
    for (OrderProductDto orderProductDto : orderProductDtos) {
      BigDecimal orderProductSum;
      if (orderProductDto.getReplaced_with() != null) {
        orderProductSum = new BigDecimal(orderProductDto.getReplaced_with().getPrice()).multiply(new BigDecimal(orderProductDto.getReplaced_with().getQuantity()));
      } else {
        orderProductSum = new BigDecimal(orderProductDto.getPrice()).multiply(new BigDecimal(orderProductDto.getQuantity()));
      }
      total = total.add(orderProductSum);
    }
    return total;
  }

  public BigDecimal salesOrderDiscountSum(BigDecimal paidSum, BigDecimal finalSum) {
    if (finalSum.compareTo(paidSum) > 0) {
      return finalSum.subtract(paidSum);
    } else {
      return new BigDecimal("0.00");
    }
  }

  public BigDecimal salesOrderReturnsSum(BigDecimal paidSum, BigDecimal finalSum) {
    if (finalSum.compareTo(paidSum) < 0) {
      return paidSum.subtract(finalSum);
    } else {
      return new BigDecimal("0.00");
    }
  }

}
