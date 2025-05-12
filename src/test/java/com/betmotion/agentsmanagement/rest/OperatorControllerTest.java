package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.DATA_LENGTH;
import static com.betmotion.agentsmanagement.utils.TestConstants.ID;
import static com.betmotion.agentsmanagement.utils.TestConstants.LENGTH;
import static com.betmotion.agentsmanagement.utils.TestConstants.OPERATOR_NEW_PASSWORD;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_4;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_OPERATOR;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_4_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.TOTAL_ELEMENTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.TOTAL_PAGES;
import static com.betmotion.agentsmanagement.utils.TestConstants.USER_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.google.common.collect.Sets.newHashSet;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.platform.api.dto.FindByIdsRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@Sql(value = {CLEAN_DB_SQL, AGENT_DATA_SQL})
class OperatorControllerTest extends AbstractIntegrationTest {

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetOperatorByIdWhenOk() throws Exception {
    mockMvc.perform(get("/api/operator")).andExpect(status().isOk())
        .andExpect(jsonPath(USER_NAME).value(PREDEFINED_OPERATOR))
        .andExpect(content().contentType(APPLICATION_JSON));
  }

  @Test
  void testGetOperatorByIdWhenIsNoPermission() throws Exception {
    mockMvc.perform(get("/api/operator"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testChangeOperatorPassword() throws Exception {
    mockMvc.perform(put("/api/operator/changePassword")
            .content(objectMapper.writeValueAsString(OPERATOR_NEW_PASSWORD))
            .contentType(APPLICATION_JSON))
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(PREDEFINED_OPERATOR))
        .andExpect(jsonPath(LENGTH).value(2))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllUsers() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(11));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 90L, "Active", 1))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/operator/allUsers?page=1&size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(22))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(22));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllUsersForAgent() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(17));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_4_1, 90L, "Active", 1))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    int agentId = agent.getId();

    mockMvc.perform(get("/api/operator/allUsers/subAgentId/" + agentId
            + "?page=0&size=2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(2))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(2));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllAgents() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(3));

    mockMvc.perform(get("/api/operator/allUsers?page=1&size=4&role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(4))
        .andExpect(jsonPath(TOTAL_PAGES).value(4))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(14));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllPlayers() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(11));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 90L, "Active", 1))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/operator/allUsers?page=0&size=1&role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(8))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(8));
  }
}
