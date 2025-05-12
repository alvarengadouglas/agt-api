package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.Constants.POWERBI_API_BASE_URI;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.betmotion.agentsmanagement.powerbi.PowerBiApi;
import com.betmotion.agentsmanagement.powerbi.api.AccessTokenDto;
import com.betmotion.agentsmanagement.powerbi.config.PowerBiTokenConfiguration;
import com.betmotion.agentsmanagement.rest.dto.PowerBiTokenDto;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping(POWERBI_API_BASE_URI)
public class PowerBiController {

  PowerBiApi powerBiApi;

  PowerBiTokenConfiguration powerBiTokenConfiguration;

  @RequestMapping(value = "/getAccessToken", method = GET)
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public PowerBiTokenDto getToken() {
    return convertToken(powerBiApi.getToken(powerBiTokenConfiguration.getBodyParams()));
  }

  private PowerBiTokenDto convertToken(AccessTokenDto token) {
    PowerBiTokenDto result = new PowerBiTokenDto();
    result.setToken(token.getTokenType() + " " + token.getAccessToken());
    return result;
  }
}
