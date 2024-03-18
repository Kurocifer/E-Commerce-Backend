package com.tutorial.ecommercebackend.model.dao;

import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenDAO extends JpaRepository<VerificationToken, Long> {

  Optional<VerificationToken> findByToken(String token);
  void deleteByUser(LocalUser user);
}
