package com.tutorial.ecommercebackend.model.dao;

import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrderDAO, Long> {

  List<WebOrder> findByUser(LocalUser user);
}

/* Not yet tested */
