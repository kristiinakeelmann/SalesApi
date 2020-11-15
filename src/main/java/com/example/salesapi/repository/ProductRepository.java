package com.example.salesapi.repository;

import com.example.salesapi.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

  Iterable<Product> findAll();

}
