package com.betmotion.agentsmanagement.service;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import com.betmotion.agentsmanagement.security.UserToken;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class SecurityService {

  RestTemplate restTemplate;

  ServicesConfiguration servicesConfiguration;

  public Optional<UserToken> validToken(String token) {
    LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("token", token);
    HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(data,
        createHeaders());
    try {
      UserToken userToken = restTemplate.postForObject(servicesConfiguration.getCheckTokenUrl(),
          requestEntity, UserToken.class);
      return ofNullable(userToken);
    } catch (ResourceAccessException | HttpClientErrorException ex) {
      log.error("Exception during token validation", ex);
      return empty();
    }
  }

  protected HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MULTIPART_FORM_DATA);
    headers.setBasicAuth(servicesConfiguration.getAuthServiceKey());
    return headers;
  }

}
