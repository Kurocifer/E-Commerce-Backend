package com.tutorial.ecommercebackend.model.dao;

import com.tutorial.ecommercebackend.model.Product;
import org.springframework.data.repository.ListCrudRepository;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}

/* Not yet tested */
