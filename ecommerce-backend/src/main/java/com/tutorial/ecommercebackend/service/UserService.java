package com.tutorial.ecommercebackend.service;

import com.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private LocalUserDAO localUserDAO;

  public UserService(LocalUserDAO localUserDAO) {
    this.localUserDAO = localUserDAO;
  }

  public LocalUser registerUser(RegistrationBody registrationBody)
        throws UserAlreadyExistsException {

    if(localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()
        || localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent())  {
      throw new UserAlreadyExistsException();
    }

    LocalUser user = new LocalUser();

    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setEmail(registrationBody.getEmail());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(registrationBody.getPassword());

    return localUserDAO.save(user);
  }
}
