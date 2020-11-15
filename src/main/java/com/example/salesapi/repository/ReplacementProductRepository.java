package com.example.salesapi.repository;

import com.example.salesapi.model.OrderReplacementProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReplacementProductRepository extends CrudRepository<OrderReplacementProduct, UUID> {

}
