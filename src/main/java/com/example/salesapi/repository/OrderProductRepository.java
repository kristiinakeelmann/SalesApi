package com.example.salesapi.repository;

import com.example.salesapi.model.OrderProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, UUID> {

  List<OrderProduct> findAllBySalesOrderId(UUID id);

}
