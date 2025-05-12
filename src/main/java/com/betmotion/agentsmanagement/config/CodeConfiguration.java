package com.betmotion.agentsmanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.agent.code")
@Data
public class CodeConfiguration {

  Integer maxAttempts;
}
