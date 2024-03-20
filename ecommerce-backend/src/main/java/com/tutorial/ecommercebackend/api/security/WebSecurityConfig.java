package com.tutorial.ecommercebackend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
public class WebSecurityConfig {

  private JWTRequestFilter jwtRequestFilter;

  public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
    this.jwtRequestFilter = jwtRequestFilter;
  }

  @Bean
  SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
    http.csrf().disable().cors().disable();
    http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
    http.authorizeHttpRequests()
            .requestMatchers("/products", "/auth/verify", "/auth/forgot").permitAll()
            .requestMatchers("/auth/register", "/auth/login").permitAll()
            .requestMatchers("/auth/reset").permitAll()
            .anyRequest().authenticated();

    return http.build();
  }

}
