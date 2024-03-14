package com.tutorial.ecommercebackend.api.controller.auth;

import com.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.tutorial.ecommercebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

  private UserService userService;

  public AuthenticationController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
    System.out.println(registrationBody);
    try {
      userService.registerUser(registrationBody);
      return ResponseEntity.ok().build();
    } catch(UserAlreadyExistsException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

  }
}
