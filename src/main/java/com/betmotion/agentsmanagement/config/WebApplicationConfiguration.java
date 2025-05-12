package com.betmotion.agentsmanagement.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class WebApplicationConfiguration {

  @Value("${spring.redis.host}")
  private String REDIS_HOST;
  @Value("${spring.redis.port}")
  private String REDIS_PORT;

  @Bean
  public RestTemplate restTemplate()
      throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    SSLContext sslContext = SSLContexts.custom()
        .loadTrustMaterial(null, (x509Certificates, s) -> true)
        .build();
    return new RestTemplateBuilder() {
      @Override
      public ClientHttpRequestFactory buildRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(
            HttpClients.custom().setSSLSocketFactory(
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
                .build());
      }
    }.build();
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer().setAddress(String.format("redis://%s:%s", REDIS_HOST, REDIS_PORT));
    RedissonClient client = Redisson.create(config);
    return client;
  }

}
