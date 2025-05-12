package com.betmotion.agentsmanagement.platform.api.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthHeaderProvider implements RequestInterceptor {

  TokenGenerator tokenGenerator;

  @Override
  public void apply(RequestTemplate template) {
    template.header(AUTHORIZATION, tokenGenerator.getToken());
  }
}
