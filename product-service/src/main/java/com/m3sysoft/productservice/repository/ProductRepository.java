package com.m3sysoft.productservice.repository;

import com.m3sysoft.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
