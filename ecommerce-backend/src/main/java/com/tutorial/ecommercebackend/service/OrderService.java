package com.tutorial.ecommercebackend.service;

import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.WebOrder;
import com.tutorial.ecommercebackend.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

  private WebOrderDAO webOrderDAO;

  public OrderService(WebOrderDAO webOrderDAO) {
    this.webOrderDAO = webOrderDAO;
  }

  public List<WebOrder> getOrders(LocalUser user) {
    return webOrderDAO.findByUser(user);
  }
}

/* Not tested */
