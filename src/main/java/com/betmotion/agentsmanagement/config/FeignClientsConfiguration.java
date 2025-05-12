package com.betmotion.agentsmanagement.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(value = "com.betmotion.agentsmanagement")
@Configuration
public class FeignClientsConfiguration {
}
