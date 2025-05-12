package com.betmotion.agentsmanagement.powerbi.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "powerbi")
@Data
public class PowerBiTokenConfiguration {

  private Map<String, Object> bodyParams;

  private String url;

}
