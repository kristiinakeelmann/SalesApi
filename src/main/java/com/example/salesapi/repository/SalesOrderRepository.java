package com.example.salesapi.repository;

import com.example.salesapi.model.SalesOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalesOrderRepository extends CrudRepository<SalesOrder, UUID> {

}
