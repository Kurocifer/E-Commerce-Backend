package com.tutorial.ecommercebackend.model.dao;

import com.tutorial.ecommercebackend.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface LocalUserDAO extends JpaRepository<LocalUser, Long> {

  Optional<LocalUser> findByUsernameIgnoreCase(String username);
  Optional<LocalUser> findByEmailIgnoreCase(String email);
}
