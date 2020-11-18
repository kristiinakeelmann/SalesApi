package com.example.salesapi.util;

import com.example.salesapi.dto.OrderUpdateDto;
import com.example.salesapi.dto.ProductDto;
import com.example.salesapi.dto.ProductUpdateDto;
import com.example.salesapi.exception.SalesApiInvalidOrderStatusException;
import com.example.salesapi.exception.SalesApiInvalidParametersException;
import com.example.salesapi.exception.SalesApiNotFoundException;
import com.example.salesapi.model.SalesOrder;
import com.example.salesapi.model.enums.Status;
import com.example.salesapi.service.OrderQueryService;
import com.example.salesapi.service.ProductQueryService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidatorUtil {

  private final OrderQueryService orderQueryService;
  private final ProductQueryService productQueryService;

  public UUID validateAndParseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new SalesApiNotFoundException();
    }
  }

  public void productExists(List<Integer> productIds) {
    for (Integer productId : productIds) {
      ProductDto productById = productQueryService.findProductById(productId);
      if (productById == null) {
        throw new SalesApiInvalidParametersException();
      }
    }
  }

  public void orderExists(UUID uuid) {
    if (orderQueryService.findSalesOrderById(uuid) == null) {
      throw new SalesApiNotFoundException();
    }
  }

  public void orderProductExists(UUID uuid) {
    if (orderQueryService.findOrderProductById(uuid) == null) {
      throw new SalesApiNotFoundException();
    }
  }

  public void orderStatusIsPaid(UUID orderId) {
    if (orderQueryService.findSalesOrderById(orderId).getStatus() != Status.PAID) {
      throw new SalesApiInvalidParametersException();
    }
  }

  public void orderStatusIsNew(UUID orderId) {
    if (orderQueryService.findSalesOrderById(orderId).getStatus() != Status.NEW) {
      throw new SalesApiInvalidParametersException();
    }
  }

  public void canUpdateOrderStatus(SalesOrder salesOrder, OrderUpdateDto orderUpdateDto) {
    if (salesOrder.getStatus() != Status.NEW || !orderUpdateDto.getStatus().equals("PAID")) {
      throw new SalesApiInvalidOrderStatusException();
    }
  }

  public void checkProductUpdate(Integer quantity) {
    if (quantity == null) {
      throw new SalesApiInvalidParametersException();
    } else if (quantity < 0) {
      throw new SalesApiInvalidParametersException();
    }
  }

  public void checkOrderUpdate(OrderUpdateDto orderUpdateDto) {
    if (orderUpdateDto.getStatus() == null) {
      throw new SalesApiInvalidParametersException();
    } else if (!EnumUtils.isValidEnum(Status.class, orderUpdateDto.getStatus())) {
      throw new SalesApiInvalidOrderStatusException();
    }
  }

  public void validateProductUpdateDto(ProductUpdateDto productUpdateDto) {
    if (productUpdateDto.getQuantity() == null && productUpdateDto.getReplaced_with() == null) {
      throw new SalesApiInvalidParametersException();
    }
  }

  public void checkProductUpdateReplacedWith(ProductUpdateDto.ReplacedWithDto replacedWithDto) {
    if (replacedWithDto.getProduct_id() == 0 || replacedWithDto.getQuantity() == 0) {
      throw new SalesApiInvalidParametersException();
    }
  }

  public void checkOrderProductId(UUID orderProductId) {
    if (orderQueryService.findOrderProductById(orderProductId) == null) {
      throw new SalesApiNotFoundException();
    }
  }

}
