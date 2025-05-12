package com.betmotion.agentsmanagement.service;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ServicesConfiguration {

  @Value("${authorization.service.key}")
  private String authServiceKey;

  @Value("${authorization.service.check.token.url}")
  private String checkTokenUrl;

  @Value(("${application.base-url}"))
  private String applicationBaseUrl;

  @Value("${authorization.allowed-origins}")
  private List<String> allowedOrigins;

}