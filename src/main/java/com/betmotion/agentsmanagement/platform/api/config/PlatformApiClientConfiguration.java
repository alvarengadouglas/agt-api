package com.betmotion.agentsmanagement.platform.api.config;

import static com.betmotion.agentsmanagement.platform.api.service.ssl.SslSocketClient.allHostsAreTrustedVerifier;
import static com.betmotion.agentsmanagement.platform.api.service.ssl.SslSocketClient.getSkippedSslSocketFactory;

import com.betmotion.agentsmanagement.platform.api.service.AuthHeaderProvider;
import com.betmotion.agentsmanagement.platform.api.service.TokenGenerator;
import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformApiClientConfiguration {

  @Bean
  public AuthHeaderProvider authHeaderProvider(TokenGenerator tokenGenerator) {
    return new AuthHeaderProvider(tokenGenerator);
  }

  @Bean
  public Client getFeignClient() {
    return new Client.Default(getSkippedSslSocketFactory(), allHostsAreTrustedVerifier());
  }
}
