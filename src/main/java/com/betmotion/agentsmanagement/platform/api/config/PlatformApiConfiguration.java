package com.betmotion.agentsmanagement.platform.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "platform.api")
@Data
@Configuration
public class PlatformApiConfiguration {

  private String internal;

  private String url;

  private String privateKey;

  private String verbose;

}