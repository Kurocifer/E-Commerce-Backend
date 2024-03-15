package com.tutorial.ecommercebackend.service;

import com.tutorial.ecommercebackend.api.model.LoginBody;
import com.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.model.dao.LocalUserDAO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  private LocalUserDAO localUserDAO;
  private EncryptionService encryptionService;
  private JWTService jwtService;

  public UserService(LocalUserDAO localUserDAO, EncryptionService encryptionService, JWTService jwtService) {
    this.localUserDAO = localUserDAO;
    this.encryptionService = encryptionService;
    this.jwtService = jwtService;
  }

  public LocalUser registerUser(RegistrationBody registrationBody)
        throws UserAlreadyExistsException {

    if(localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()
        || localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent())  {
      throw new UserAlreadyExistsException();
    }

    LocalUser user = new LocalUser();
    String encryptedPassword = encryptionService
                                  .encryptPassword(registrationBody.getPassword());

    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setEmail(registrationBody.getEmail());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(encryptedPassword);

    return localUserDAO.save(user);
  }

  public String loginUser(LoginBody loginBody) {
    Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());

    if(opUser.isPresent()) {
      LocalUser user = opUser.get();
      if(encryptionService.verifyPassword(loginBody.getPassword(), user.getPassword())) {
        return jwtService.generateJWT(user);
      }

    }
    return null;
  }
}
