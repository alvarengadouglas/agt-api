package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_4;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.base.Charsets.UTF_8;
import static java.net.URLEncoder.encode;
import static java.util.stream.Collectors.joining;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.powerbi.api.AccessTokenDto;
import com.betmotion.agentsmanagement.powerbi.api.PowerBiErrorDto;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = {CLEAN_DB_SQL, AGENT_DATA_SQL})
class PowerBiControllerTest extends AbstractIntegrationTest {

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testGetToken() throws Exception {
    String body = powerBiTokenConfiguration.getBodyParams()
        .entrySet()
        .stream().map(entry -> entry.getKey() + "=" + encode((String) entry.getValue(),
            UTF_8))
        .collect(joining("&"));
    AccessTokenDto result = new AccessTokenDto();
    result.setTokenType("Bearer");
    result.setAccessToken("AA");
    stubFor(WireMock.post(urlEqualTo("/oauth2/v2.0/token"))
        .withHeader(CONTENT_TYPE,
            new EqualToPattern(APPLICATION_FORM_URLENCODED_VALUE
                + "; charset=" + UTF_8.name()))
        .withRequestBody(new EqualToPattern(body))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(result))));

    mockMvc.perform(get("/api/powerbi/getAccessToken"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$.token").value("Bearer AA"));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testGetTokenWhenExceptionHappened() throws Exception {
    String errorDescriptionFromPowerBi = "Error from Power BI";
    String body = powerBiTokenConfiguration.getBodyParams()
        .entrySet()
        .stream().map(entry -> entry.getKey() + "=" + encode((String) entry.getValue(),
            UTF_8))
        .collect(joining("&"));
    PowerBiErrorDto result = new PowerBiErrorDto();
    result.setError("AAA");
    result.setErrorDescription(errorDescriptionFromPowerBi);
    stubFor(WireMock.post(urlEqualTo("/oauth2/v2.0/token"))
        .withHeader(CONTENT_TYPE,
            new EqualToPattern(APPLICATION_FORM_URLENCODED_VALUE
                + "; charset=" + UTF_8.name()))
        .withRequestBody(new EqualToPattern(body))
        .willReturn(aResponse()
            .withStatus(BAD_REQUEST.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(result))));

    mockMvc.perform(get("/api/powerbi/getAccessToken"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$.globalErrors.length()").value(1))
        .andExpect(jsonPath("$.globalErrors[0]").value(errorDescriptionFromPowerBi))
        .andExpect(jsonPath("$.fieldErrors.length()").value(0))
    ;
  }
}
