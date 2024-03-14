package com.tutorial.ecommercebackend.api.model;

import jakarta.validation.constraints.*;

public class RegistrationBody {

  @NotNull
  @NotBlank
  @Size(min = 3, max = 255)
  private String username;
  @NotNull
  @NotBlank
  @Size(min = 8, max = 32)
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
  private String password;

  @Email
  @NotNull
  @NotBlank
  private String email;
  @NotNull
  @NotBlank
  private String firstName;
  @NotNull
  @NotBlank
  private String lastName;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public String toString() {
    return "RegistrationBody{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", email='" + email + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            '}';
  }
}
