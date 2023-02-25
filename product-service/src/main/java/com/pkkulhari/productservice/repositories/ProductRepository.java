package com.pkkulhari.productservice.repositories;

import com.pkkulhari.productservice.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
