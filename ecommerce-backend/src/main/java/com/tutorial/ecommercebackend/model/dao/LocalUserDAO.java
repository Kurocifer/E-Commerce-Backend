package com.tutorial.ecommercebackend.model.dao;

import com.tutorial.ecommercebackend.model.LocalUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocalUserDAO extends CrudRepository<LocalUser, Long> {

  Optional<LocalUser> findByUsernameIgnoreCase(String username);
  Optional<LocalUser> findByEmailIgnoreCase(String email);
}
