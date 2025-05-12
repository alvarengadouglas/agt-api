package com.betmotion.agentsmanagement.powerbi.config;

import static com.betmotion.agentsmanagement.platform.api.service.ssl.SslSocketClient.allHostsAreTrustedVerifier;
import static com.betmotion.agentsmanagement.platform.api.service.ssl.SslSocketClient.getSkippedSslSocketFactory;

import feign.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PowerBiClientConfiguration {

  @Bean
  public Client powerBiFeignClient() {
    return new Client.Default(getSkippedSslSocketFactory(), allHostsAreTrustedVerifier());
  }
}
