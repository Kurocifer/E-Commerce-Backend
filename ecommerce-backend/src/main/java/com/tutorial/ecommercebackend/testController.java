package com.tutorial.ecommercebackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testController {

  @GetMapping("/test")
  public String testMethod() {
    return "This woks fine";
  }
}
