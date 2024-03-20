package com.tutorial.ecommercebackend.api.controller.auth;

import com.tutorial.ecommercebackend.api.model.LoginBody;
import com.tutorial.ecommercebackend.api.model.LoginResponse;
import com.tutorial.ecommercebackend.api.model.PasswordResetBody;
import com.tutorial.ecommercebackend.api.model.RegistrationBody;
import com.tutorial.ecommercebackend.exception.EmailFailureException;
import com.tutorial.ecommercebackend.exception.EmailNotFoundException;
import com.tutorial.ecommercebackend.exception.UserAlreadyExistsException;
import com.tutorial.ecommercebackend.exception.UserNotVerifiedException;
import com.tutorial.ecommercebackend.model.LocalUser;
import com.tutorial.ecommercebackend.service.UserService;
import jakarta.validation.Valid;
import org.apache.juli.logging.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
    }
  }

  @PostMapping("/login")
  public  ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
    String jwt = null;
    try {
      jwt = userService.loginUser(loginBody);
    } catch (UserNotVerifiedException e) {
      LoginResponse response = new LoginResponse();
      response.setSuccess(false);
      String reason = "USER_NOT_VERIFIED";
      if(e.isNewEmailSent()) {
        reason += "_EMAIL_RESENT";
      }
      response.setFailureReason(reason);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if(jwt == null)
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    else {
      LoginResponse response = new LoginResponse();
      response.setJwt(jwt);
      response.setSuccess(true);
      return ResponseEntity.ok(response);
    }
  }

  @PostMapping("/verify")
  public ResponseEntity verifyEmail(@RequestParam String token) {
    if(userService.verifyUser(token)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
  }

  @GetMapping("/me")
  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
    return user;
  }

  @PostMapping("/forgot")
  public ResponseEntity forgotPassword(@RequestParam String email) {
    try {
      userService.forgotPassword(email);
      return ResponseEntity.ok().build();
    } catch (EmailNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    } catch (EmailFailureException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/reset")
  public ResponseEntity<Object> resetPassword(@Valid @RequestBody PasswordResetBody body) {
    userService.resetPassword(body);
    return ResponseEntity.ok().build();
  }
}
