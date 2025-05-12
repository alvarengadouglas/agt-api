package com.betmotion.agentsmanagement.powerbi;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.betmotion.agentsmanagement.powerbi.api.AccessTokenDto;
import com.betmotion.agentsmanagement.powerbi.config.PowerBiClientConfiguration;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "powerBIAPI", configuration = PowerBiClientConfiguration.class,
    url = "${powerbi.url}")
public interface PowerBiApi {

  @RequestMapping(value = "/oauth2/v2.0/token", method = RequestMethod.POST,
      consumes = APPLICATION_FORM_URLENCODED_VALUE, produces = APPLICATION_JSON_VALUE)
  AccessTokenDto getToken(@RequestBody Map<String, ?> params);
}
