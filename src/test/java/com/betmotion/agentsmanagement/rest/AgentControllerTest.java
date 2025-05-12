package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE;
import static com.betmotion.agentsmanagement.domain.UserStatus.BLOCKED;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.CREDIT;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.DEBIT;
import static com.betmotion.agentsmanagement.utils.JsonUtils.extractValueFromJson;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_CAN_HAVE_SUB_AGENTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_CAN_HAVE_SUB_AGENTS_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_COMMISSION;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_COMMISSION_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_EMAIL_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_FULL_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_FULL_NAME_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_NEW_PASSWORD;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_PASSWORD;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_PHONE_NUMBER_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_USER_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_USER_NAME_UPDATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.CAN_HAVE_SUB_AGENTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CODE;
import static com.betmotion.agentsmanagement.utils.TestConstants.COMMISSION;
import static com.betmotion.agentsmanagement.utils.TestConstants.CREATED_DATE;
import static com.betmotion.agentsmanagement.utils.TestConstants.CREATED_ON;
import static com.betmotion.agentsmanagement.utils.TestConstants.DATA_LENGTH;
import static com.betmotion.agentsmanagement.utils.TestConstants.EMAIL;
import static com.betmotion.agentsmanagement.utils.TestConstants.FULL_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.HIERARCHY;
import static com.betmotion.agentsmanagement.utils.TestConstants.ID;
import static com.betmotion.agentsmanagement.utils.TestConstants.LENGTH;
import static com.betmotion.agentsmanagement.utils.TestConstants.NUMBER_OF_DIRECT_PLAYERS;
import static com.betmotion.agentsmanagement.utils.TestConstants.NUMBER_OF_DIRECT_SUBAGENTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.PARENT_USER_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.PHONE_NUMBER;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_3;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_OPERATOR;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_2_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_SUBAGENT_3_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_SUBAGENT_3_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_SUBAGENT_3_1_2;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_SUBAGENT_4_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.ROLE;
import static com.betmotion.agentsmanagement.utils.TestConstants.TOTAL_ELEMENTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.TOTAL_PAGES;
import static com.betmotion.agentsmanagement.utils.TestConstants.USER_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.com.google.common.collect.Sets.newHashSet;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.AgentTransaction;
import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.domain.Wallet;
import com.betmotion.agentsmanagement.domain.WalletTransaction;
import com.betmotion.agentsmanagement.platform.api.dto.ChangeStatusRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.ChangeStatusResponseDto;
import com.betmotion.agentsmanagement.platform.api.dto.FindByIdsRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.betmotion.agentsmanagement.platform.api.dto.UserStatus;
import com.betmotion.agentsmanagement.rest.dto.CollectAgentDto;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.PayoutAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentCreditOperation;
import com.betmotion.agentsmanagement.rest.dto.agent.CreateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.UpdateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@Sql(value = {CLEAN_DB_SQL, AGENT_DATA_SQL})
class AgentControllerTest extends AbstractIntegrationTest {


  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAgentByIdWhenOk() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andReturn();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    mockMvc.perform(get("/api/agent/" + id)
        ).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAgentByIdWhenNoFound() throws Exception {
    mockMvc.perform(get("/api/agent/" + Integer.MAX_VALUE))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testGetAgentByIdWhenNoPermissionAndForbidden() throws Exception {
    mockMvc.perform(get("/api/agent/1"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testCreateAgentByAgentOk() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10))
        .andReturn();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    String locationFromHeaders = mvcResult.getResponse().getHeader(LOCATION);
    String calculatedUri =
        servicesConfiguration.getApplicationBaseUrl() + "/api/agent" + "/" + id;
    assertThat(locationFromHeaders, equalTo(calculatedUri));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testCreateAgentByOperatorOk() throws Exception {
    Agent operator = getAgentForCurrentUser();
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10))
        .andReturn();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    String locationFromHeaders = mvcResult.getResponse().getHeader(LOCATION);
    String calculatedUri =
        servicesConfiguration.getApplicationBaseUrl() + "/api/agent/" + id;
    assertThat(locationFromHeaders, equalTo(calculatedUri));
    Agent entity = agentRepository.getEntity(id);
    assertThat(entity.getParentId(), equalTo(operator.getId()));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testCreateAgentWhenAlreadyExists() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    dto.setUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_3);
    mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()").value(2L))
        .andExpect(jsonPath("$.globalErrors.length()").value(1L))
        .andExpect(jsonPath("$.globalErrors[0]").value(
            "Agent with name agentWithSub_3 already exists"))
        .andExpect(jsonPath("$.fieldErrors.length()").value(0L));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testCreateAgentByAgentWhenCanNotCreateSubagents() throws Exception {
    AppUserDetails currentUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent agent = agentRepository.getEntity(currentUserDetails.getId());
    agent.setCanHaveSubAgents(false);
    agentRepository.save(agent);
    CreateAgentDto dto = getCreateAgentDto();
    mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
        .andExpect(jsonPath(LENGTH).value(2))
        .andExpect(jsonPath("$.globalErrors.length()").value(1))
        .andExpect(jsonPath("$.globalErrors[0]").value("Current agent can not create subagents."))
        .andExpect(jsonPath("$.fieldErrors.length()").value(0));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testUpdateAgentByAgentOk() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10))
        .andReturn();

    UpdateAgentDto updateAgentDto = getUpdateAgentDto();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    mockMvc.perform(put("/api/agent/" + id)
            .content(objectMapper.writeValueAsString(updateAgentDto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME_UPDATE))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME_UPDATE))
        .andExpect(jsonPath(EMAIL).value(AGENT_EMAIL_UPDATE))
        .andExpect(jsonPath(PHONE_NUMBER).value(AGENT_PHONE_NUMBER_UPDATE))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION_UPDATE.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS_UPDATE))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10));
  }

  @Test
  void testUpdateAgentByIdWhenNoPermissionAndForbidden() throws Exception {
    mockMvc.perform(put("/api/agent/1"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testUpdateAgentByIdWhenNoFound() throws Exception {
    UpdateAgentDto updateAgentDto = getUpdateAgentDto();
    mockMvc.perform(put("/api/agent/" + Integer.MAX_VALUE)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateAgentDto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testDeleteAgentWhenIsOk() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andReturn();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    mockMvc.perform(delete("/api/agent/" + id)
        ).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10));

    mockMvc.perform(get("/api/agent/" + id)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAgentListByParentIdWhenOk() throws Exception {
    Agent agent = getAgentForCurrentUser();
    mockMvc.perform(get("/api/agent/parent/" + agent.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.data", hasSize(2)))
        .andExpect(jsonPath("$.totalPages").value(1))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testChangeAgentPassword() throws Exception {
    CreateAgentDto dto = getCreateAgentDto();
    MvcResult mvcResult = mockMvc.perform(post("/api/agent")
            .content(objectMapper.writeValueAsString(dto))
            .contentType(APPLICATION_JSON)
        ).andExpect(status().isCreated())
        .andReturn();
    Integer id = extractValueFromJson(ID, mvcResult.getResponse().getContentAsString());
    mockMvc.perform(put("/api/agent/" + id + "/changePassword")
            .content(objectMapper.writeValueAsString(AGENT_NEW_PASSWORD))
            .contentType(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(AGENT_USER_NAME))
        .andExpect(jsonPath(FULL_NAME).value(AGENT_FULL_NAME))
        .andExpect(jsonPath(COMMISSION).value(AGENT_COMMISSION.floatValue()))
        .andExpect(jsonPath(CAN_HAVE_SUB_AGENTS).value(AGENT_CAN_HAVE_SUB_AGENTS))
        .andExpect(jsonPath(CODE).isNotEmpty())
        .andExpect(jsonPath(CREATED_DATE).isNotEmpty())
        .andExpect(jsonPath(HIERARCHY).hasJsonPath())
        .andExpect(jsonPath(LENGTH).value(10));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsers() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(13));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/allUsers?page=1&size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(10))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(10));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersForSubAgentWithSubAgent() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    int subAgentId = agent.getId();

    mockMvc.perform(
            get("/api/agent/allUsers/subAgentId/" + subAgentId
                + "?page=0&size=5&withSubAgent=true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(2))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(2));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersForSubAgent() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    int subAgentId = agent.getId();

    mockMvc.perform(get("/api/agent/allUsers/subAgentId/" + subAgentId
            + "?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(1));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1)
  void testGetDirectUsers() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 50L, "Active", 2))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/directUsers?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(3))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(3));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1)
  void testGetDirectUsersForRoleAgent() throws Exception {
    mockMvc.perform(get("/api/agent/directUsers?page=0&size=5&role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(2))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(2));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1)
  void testGetDirectUsersForRolePlayer() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 50L, "Active", 2))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/directUsers?page=0&size=5&role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(1));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1)
  void testGetDirectUsersForSubAgent() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(13));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 50L, "Active", 2))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    int subAgentId = agent.getId();

    mockMvc.perform(get("/api/agent/directUsers/" + subAgentId + "?page=0&size=5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(1))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(1));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersForAgentsWithFilterAgents() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(3));

    mockMvc.perform(get("/api/agent/allUsers?page=0&size=4&role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(4))
        .andExpect(jsonPath(TOTAL_PAGES).value(2))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(5));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersForAgentWithFilterPlayer() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12, 13, 14));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 90L, "Active", 1))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_2_1_1, 120L, "Active", 4))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/allUsers?page=0&size=3&role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(3))
        .andExpect(jsonPath(TOTAL_PAGES).value(2))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(5));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllUsersForOperator() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(3));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/allUsers?page=1&size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(22))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(22));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllUsersForOperatorWithFilterAgent() throws Exception {
    mockMvc.perform(get("/api/agent/allUsers?page=1&size=1&role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(14))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(14));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetAllUsersForOperatorWithFilterPlayer() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/allUsers?page=1&size=1&role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(1))
        .andExpect(jsonPath(TOTAL_PAGES).value(8))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(8));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetUserDetailInfoForAgent() throws Exception {
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_2);
    int agentId = agent.getId();

    mockMvc.perform(
            get("/api/agent/user/detail/" + agentId + "?role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(11))
        .andExpect(jsonPath(ID).value(agent.getId()))
        .andExpect(jsonPath(USER_NAME).value(PREDEFINED_SUBAGENT_3_1_2))
        .andExpect(jsonPath(FULL_NAME).value("subagent_3_1_2_FN"))
        .andExpect(jsonPath(EMAIL).value("subagent_3_1_2@gmail.com"))
        .andExpect(jsonPath(ROLE).value(AGENT))
        .andExpect(jsonPath(CREATED_ON).value("20210702"))
        .andExpect(jsonPath(PARENT_USER_NAME).value(PREDEFINED_SUBAGENT_3_1))
        .andExpect(jsonPath(NUMBER_OF_DIRECT_SUBAGENTS).value(1))
        .andExpect(jsonPath(NUMBER_OF_DIRECT_PLAYERS).value(0))
        .andExpect(jsonPath("$.extendedInfoAgentDto").isNotEmpty());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetUserDetailInfoForPlayer() throws Exception {
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1_1);
    int userId = player.getPlatformId();

    FindByIdsRequestDto requestDtoForPlayer = new FindByIdsRequestDto();
    requestDtoForPlayer.setUserIds(newHashSet(13));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 90L, "Active", 1,
            "player_3_1_1_1@gmail.com"))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDtoForPlayer)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(
            get("/api/agent/user/detail/" + userId + "?role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(11))
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(USER_NAME).value(PREDEFINED_PLAYER_3_1_1_1))
        .andExpect(jsonPath(FULL_NAME).value("player_3_1_1_1"))
        .andExpect(jsonPath(EMAIL).value("player_3_1_1_1@gmail.com"))
        .andExpect(jsonPath(ROLE).value(PLAYER))
        .andExpect(jsonPath(PARENT_USER_NAME).value(PREDEFINED_SUBAGENT_3_1_1))
        .andExpect(jsonPath("$.extendedInfoAgentDto").isNotEmpty());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersWithSearchString() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12, 13, 14));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 90L, "Active", 1))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_2_1_1, 120L, "Active", 4))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/searchUsers?page=0&size=3&search=play"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(3))
        .andExpect(jsonPath(TOTAL_PAGES).value(2))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(5));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersAsAgentsWithSearchString() throws Exception {
    mockMvc.perform(get("/api/agent/searchUsers?page=0&size=3&search=play&role=AGENT"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(0))
        .andExpect(jsonPath(TOTAL_PAGES).value(0))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(0));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersAsPlayersWithSearchString() throws Exception {
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(newHashSet(12, 13, 14));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1, 90L, "Active", 1))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 120L, "Active", 3))
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_2_1_1, 120L, "Active", 4))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    mockMvc.perform(get("/api/agent/searchUsers?page=0&size=3&search=play&role=PLAYER"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath(DATA_LENGTH).value(3))
        .andExpect(jsonPath(TOTAL_PAGES).value(2))
        .andExpect(jsonPath(TOTAL_ELEMENTS).value(5));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testGetAllUsersWithNotValidSearchStringReturnException() throws Exception {
    mockMvc.perform(get("/api/agent/searchUsers?page=0&size=20&search=pl"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  @Transactional(readOnly = true)
  void testGetExtendedInfoForAgent() throws Exception {
    Agent agent = getAgentForCurrentUser();
    mockMvc.perform(get("/api/agent/" + agent.getId() + "/extendedInfo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath("$.userName").value(PREDEFINED_AGENT_WITH_SUBAGENTS_3))
        .andExpect(jsonPath("$.id").value(agent.getId()))
        .andExpect(jsonPath("$.child.length()").value(2L))
        .andExpect(jsonPath("$.child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[0].userName").value("subagent_3_1"))
        .andExpect(jsonPath("$.child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[0].child").isNotEmpty())
        .andExpect(jsonPath("$.child[0].child.length()").value(2L))
        .andExpect(jsonPath("$.child[0].child[0].userName").value("subagent_3_1_1"))
        .andExpect(jsonPath("$.child[0].child[0].id").value("5"))
        .andExpect(jsonPath("$.child[0].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[0].child[1].userName").value("subagent_3_1_2"))
        .andExpect(jsonPath("$.child[0].child[1].id").value("6"))
        .andExpect(jsonPath("$.child[0].child[1].child").isNotEmpty())
        .andExpect(jsonPath("$.child[0].child[1].child[0].userName").value("subagent_3_1_2_1"))
        .andExpect(jsonPath("$.child[0].child[1].child[0].id").value("7"))
        .andExpect(jsonPath("$.child[0].child[1].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[1].length()").value(3L))
        .andExpect(jsonPath("$.child[1].userName").value("subagent_3_2"))
        .andExpect(jsonPath("$.child[1].id").isNotEmpty())
        .andExpect(jsonPath("$.child[1].child").isEmpty());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  @Transactional(readOnly = true)
  void testGetExtendedInfoForOperator() throws Exception {
    Agent operator = getAgentForCurrentUser();
    mockMvc.perform(get("/api/agent/" + operator.getId() + "/extendedInfo"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(3))
        .andExpect(jsonPath("$.userName").value(operator.getUser().getUserName()))
        .andExpect(jsonPath("$.id").value(operator.getId()))
        .andExpect(jsonPath("$.child.length()").value(6L))
        .andExpect(jsonPath("$.child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[0].userName").value("agentNoSub_1"))
        .andExpect(jsonPath("$.child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[1].length()").value(3L))
        .andExpect(jsonPath("$.child[1].userName").value("agentNoSub_2"))
        .andExpect(jsonPath("$.child[1].id").isNotEmpty())
        .andExpect(jsonPath("$.child[1].child").isEmpty())
        .andExpect(jsonPath("$.child[2].length()").value(3L))
        .andExpect(jsonPath("$.child[2].userName").value("agentWithSub_3"))
        .andExpect(jsonPath("$.child[2].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[2].child[0].userName").value("subagent_3_1"))
        .andExpect(jsonPath("$.child[2].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[2].child[0].child[0].userName").value("subagent_3_1_1"))
        .andExpect(jsonPath("$.child[2].child[0].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[1].length()").value(3L))
        .andExpect(jsonPath("$.child[2].child[0].child[1].userName").value("subagent_3_1_2"))
        .andExpect(jsonPath("$.child[2].child[0].child[1].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[1].child").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[1].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[2].child[0].child[1].child[0].userName")
            .value("subagent_3_1_2_1"))
        .andExpect(jsonPath("$.child[2].child[0].child[1].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[0].child[1].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[2].child[1].length()").value(3L))
        .andExpect(jsonPath("$.child[2].child[1].userName").value("subagent_3_2"))
        .andExpect(jsonPath("$.child[2].child[1].id").isNotEmpty())
        .andExpect(jsonPath("$.child[2].child[1].child").isEmpty())
        .andExpect(jsonPath("$.child[3].length()").value(3L))
        .andExpect(jsonPath("$.child[3].userName").value("agentWithSub_4"))
        .andExpect(jsonPath("$.child[3].id").isNotEmpty())
        .andExpect(jsonPath("$.child[3].child").isNotEmpty())
        .andExpect(jsonPath("$.child[3].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[3].child[0].userName").value("subagent_4_1"))
        .andExpect(jsonPath("$.child[3].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[3].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[4].length()").value(3L))
        .andExpect(jsonPath("$.child[4].userName").value("agentWithSub_5"))
        .andExpect(jsonPath("$.child[4].id").isNotEmpty())
        .andExpect(jsonPath("$.child[4].child").isNotEmpty())
        .andExpect(jsonPath("$.child[4].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[4].child[0].userName").value("subagent_5_1"))
        .andExpect(jsonPath("$.child[4].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[4].child[0].child").isEmpty())
        .andExpect(jsonPath("$.child[5].length()").value(3L))
        .andExpect(jsonPath("$.child[5].userName").value("agentWithSub_6"))
        .andExpect(jsonPath("$.child[5].id").isNotEmpty())
        .andExpect(jsonPath("$.child[5].child").isNotEmpty())
        .andExpect(jsonPath("$.child[5].child[0].length()").value(3L))
        .andExpect(jsonPath("$.child[5].child[0].userName").value("subagent_6_1"))
        .andExpect(jsonPath("$.child[5].child[0].id").isNotEmpty())
        .andExpect(jsonPath("$.child[5].child[0].child").isEmpty());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testBlockAgent() throws Exception {
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    int agentId = agent.getId();
    mockMvc.perform(put("/api/agent/" + agentId + "/block"))
        .andExpect(status().isAccepted());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testBlockAgentThrowExceptionNotSubAgent() throws Exception {
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_4_1);
    int notSubAgentId = agent.getId();
    mockMvc.perform(put("/api/agent/" + notSubAgentId + "/block"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testBlockAgentThrowExceptionNotValidAgentId() throws Exception {
    mockMvc.perform(put("/api/agent/" + Integer.MAX_VALUE + "/block"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testUnblockAgent() throws Exception {
    User userForAgent = userRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    int agentId = agent.getId();
    assertThat(userForAgent.getStatus(), equalTo(ACTIVE));
    mockMvc.perform(put("/api/agent/" + agentId + "/block"))
        .andExpect(status().isAccepted());
    User blockedAgent = userRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    assertThat(blockedAgent.getStatus(), equalTo(BLOCKED));
    mockMvc.perform(put("/api/agent/" + agentId + "/unblock"))
        .andExpect(status().isAccepted());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testBlockAndUnblockPlayer() throws Exception {
    FindByIdsRequestDto requestDtoForPlayer = new FindByIdsRequestDto();
    requestDtoForPlayer.setUserIds(newHashSet(12));
    List<PlatformUserDto> items = ImmutableList.<PlatformUserDto>builder()
        .add(buildPlatformUserDto(PREDEFINED_PLAYER_3_1_1_1, 90L, "Active", 1))
        .build();

    stubFor(WireMock.post(urlEqualTo("/agent/user/find-by-ids.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDtoForPlayer)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(items))));

    PageData<UserInfoDto> players = agentService.getAllUsers(PageRequest.of(0, 1),
        UserRole.PLAYER);
    UserInfoDto player = players.getData().get(0);

    ChangeStatusRequestDto requestDto = new ChangeStatusRequestDto();
    requestDto.setUserName(player.getUserName());
    requestDto.setStatus(UserStatus.BLOCKED);
    ChangeStatusResponseDto responseDto = new ChangeStatusResponseDto();
    responseDto.setUserName(player.getUserName());
    responseDto.setStatus(UserStatus.BLOCKED);

    stubFor(WireMock.post(urlEqualTo("/agent/user/change-status.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(responseDto))));

    mockMvc.perform(put("/api/agent/player/" + player.getId() + "/block"))
        .andExpect(status().isAccepted());

    requestDto = new ChangeStatusRequestDto();
    requestDto.setUserName(player.getUserName());
    requestDto.setStatus(UserStatus.ACTIVE);
    responseDto = new ChangeStatusResponseDto();
    responseDto.setUserName(player.getUserName());
    responseDto.setStatus(UserStatus.ACTIVE);

    stubFor(WireMock.post(urlEqualTo("/agent/user/change-status.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(responseDto))));

    mockMvc.perform(put("/api/agent/player/" + player.getId() + "/unblock"))
        .andExpect(status().isAccepted());

    verify(2, postRequestedFor(urlEqualTo("/agent/user/change-status.do")));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetCreditBalanceForOperator() throws Exception {
    Long newBalance = 1900L;
    Wallet creditsWallet = creditsService.getCreditWalletForCurrentUser();
    creditsWallet.setBalance(newBalance);
    walletRepository.saveAndFlush(creditsWallet);
    mockMvc.perform(get("/api/agent/creditBalance"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(1))
        .andExpect(jsonPath("$.amount").value(newBalance));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1_1)
  void testGetCreditBalanceForAgent() throws Exception {
    Long newBalance = 1900L;
    Wallet creditsWallet = creditsService.getCreditWalletForCurrentUser();
    creditsWallet.setBalance(newBalance);
    walletRepository.saveAndFlush(creditsWallet);
    mockMvc.perform(get("/api/agent/creditBalance"))
        .andExpect(status().isOk())
        .andExpect(jsonPath(LENGTH).value(1))
        .andExpect(jsonPath("$.amount").value(newBalance));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  @Disabled
  void testCollectSubAgentWalletThrowExceptionNotEnoughInWallet() throws Exception {
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    CollectAgentDto collectAgentDto = new CollectAgentDto();
    collectAgentDto.setAmount(5000L);
    collectAgentDto.setNote("Some notes");
    collectAgentDto.setAgentId(agent.getId());
    mockMvc.perform(put("/api/agent/collect")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(collectAgentDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testCollectSubAgentWallet() throws Exception {
    Long amount = 3L;
    Long currentSubAgentBalance = 100L;
    String note = "Some note";
    Agent agent = getAgentForCurrentUser();
    Wallet agentWalletBefore = walletRepository.getEntity(agent.getWalletId());
    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    subAgent.setBalance(currentSubAgentBalance);
    agentRepository.save(subAgent);
    Wallet subAgentWalletBefore = walletRepository.getEntity(subAgent.getWalletId());
    CollectAgentDto collectAgentDto = new CollectAgentDto();
    collectAgentDto.setAmount(amount);
    collectAgentDto.setNote(note);
    collectAgentDto.setAgentId(subAgent.getId());
    mockMvc.perform(put("/api/agent/collect")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(collectAgentDto)))
        .andExpect(status().isAccepted());

    Wallet agentWalletAfter = walletRepository.getEntity(agent.getWalletId());
    Wallet subAgentWalletAfter = walletRepository.getEntity(subAgent.getWalletId());
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletAfter.getId()));
    assertThat(agentWalletBefore.getBalance(),
        equalTo(agentWalletAfter.getBalance() - amount));
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletAfter.getId()));
    assertThat(subAgentWalletBefore.getBalance(),
        equalTo(subAgentWalletAfter.getBalance() + amount));

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getWalletId()).get(0);
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(agentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(agentWalletTransaction.getNote()));
    assertThat(agentWalletTransaction.getBalance(), equalTo(agentWalletAfter.getBalance()));

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getWalletId()).get(0);
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(subAgentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(subAgentWalletTransaction.getNote()));
    assertThat(subAgentWalletTransaction.getBalance(), equalTo(subAgentWalletAfter.getBalance()));

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(note));
    assertThat(agentTransaction.getBalance(), equalTo(currentSubAgentBalance - amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.REMOVE_SALDO));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testCollectSubAgentWalletByOperator() throws Exception {
    Long amount = 3L;
    Long currentSubAgentBalance = 100L;
    String note = "Some note";
    Agent agent = getAgentForCurrentUser();
    Wallet agentWalletBefore = walletRepository.getEntity(agent.getWalletId());
    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    subAgent.setBalance(currentSubAgentBalance);
    agentRepository.save(subAgent);
    Wallet subAgentWalletBefore = walletRepository.getEntity(subAgent.getWalletId());
    CollectAgentDto collectAgentDto = new CollectAgentDto();
    collectAgentDto.setAmount(amount);
    collectAgentDto.setNote(note);
    collectAgentDto.setAgentId(subAgent.getId());
    mockMvc.perform(put("/api/agent/collect")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(collectAgentDto)))
        .andExpect(status().isAccepted());

    Wallet agentWalletAfter = walletRepository.getEntity(agent.getWalletId());
    Wallet subAgentWalletAfter = walletRepository.getEntity(subAgent.getWalletId());
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletAfter.getId()));
    assertThat(agentWalletBefore.getBalance(),
        equalTo(agentWalletAfter.getBalance() - amount));
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletAfter.getId()));
    assertThat(subAgentWalletBefore.getBalance(),
        equalTo(subAgentWalletAfter.getBalance() + amount));

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getWalletId()).get(0);
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(agentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(agentWalletTransaction.getNote()));
    assertThat(agentWalletTransaction.getBalance(), equalTo(agentWalletAfter.getBalance()));

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getWalletId()).get(0);
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(subAgentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(subAgentWalletTransaction.getNote()));
    assertThat(subAgentWalletTransaction.getBalance(), equalTo(subAgentWalletAfter.getBalance()));

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(note));
    assertThat(agentTransaction.getBalance(), equalTo(currentSubAgentBalance - amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.REMOVE_SALDO));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  @Disabled
  void testPayoutSubAgentWalletThrowExceptionNotEnoughInWallet() throws Exception {
    Agent agent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    PayoutAgentDto payoutAgentDto = new PayoutAgentDto();
    payoutAgentDto.setNote("Some note");
    payoutAgentDto.setAmount(5000L);
    payoutAgentDto.setAgentId(agent.getId());
    mockMvc.perform(put("/api/agent/payout")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payoutAgentDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testPayoutSubAgentWallet() throws Exception {
    Long amount = 3L;
    Long currentSubAgentBalance = 100L;
    String note = "Some note";
    Agent agent = getAgentForCurrentUser();
    Wallet agentWalletBefore = walletRepository.getEntity(agent.getWalletId());
    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    subAgent.setBalance(currentSubAgentBalance);
    agentRepository.save(subAgent);
    Wallet subAgentWalletBefore = walletRepository.getEntity(subAgent.getWalletId());
    PayoutAgentDto payoutAgentDto = new PayoutAgentDto();
    payoutAgentDto.setNote(note);
    payoutAgentDto.setAmount(amount);
    payoutAgentDto.setAgentId(subAgent.getId());
    mockMvc.perform(put("/api/agent/payout")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payoutAgentDto)))
        .andExpect(status().isAccepted());

    Wallet agentWalletAfter = walletRepository.getEntity(agent.getWalletId());
    Wallet subAgentWalletAfter = walletRepository.getEntity(subAgent.getWalletId());
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletAfter.getId()));
    assertThat(agentWalletBefore.getBalance(),
        equalTo(agentWalletAfter.getBalance() + amount));
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletAfter.getId()));
    assertThat(subAgentWalletBefore.getBalance(),
        equalTo(subAgentWalletAfter.getBalance() - amount));

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getWalletId()).get(0);
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(agentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(agentWalletTransaction.getNote()));
    assertThat(agentWalletAfter.getBalance(), equalTo(agentWalletTransaction.getBalance()));

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getWalletId()).get(0);
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(subAgentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(subAgentWalletTransaction.getNote()));
    assertThat(subAgentWalletAfter.getBalance(), equalTo(subAgentWalletTransaction.getBalance()));

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(note));
    assertThat(agentTransaction.getBalance(), equalTo(currentSubAgentBalance + amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.ADD_SALDO));
  }


  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testPayoutSubAgentWalletByOperator() throws Exception {
    Long amount = 3L;
    Long currentSubAgentBalance = 100L;
    String note = "Some note";
    Agent agent = getAgentForCurrentUser();
    Wallet agentWalletBefore = walletRepository.getEntity(agent.getWalletId());
    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    subAgent.setBalance(currentSubAgentBalance);
    agentRepository.save(subAgent);
    Wallet subAgentWalletBefore = walletRepository.getEntity(subAgent.getWalletId());
    PayoutAgentDto payoutAgentDto = new PayoutAgentDto();
    payoutAgentDto.setNote(note);
    payoutAgentDto.setAmount(amount);
    payoutAgentDto.setAgentId(subAgent.getId());
    mockMvc.perform(put("/api/agent/payout")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payoutAgentDto)))
        .andExpect(status().isAccepted());

    Wallet agentWalletAfter = walletRepository.getEntity(agent.getWalletId());
    Wallet subAgentWalletAfter = walletRepository.getEntity(subAgent.getWalletId());
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletAfter.getId()));
    assertThat(agentWalletBefore.getBalance(),
        equalTo(agentWalletAfter.getBalance() + amount));
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletAfter.getId()));
    assertThat(subAgentWalletBefore.getBalance(),
        equalTo(subAgentWalletAfter.getBalance() - amount));

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getWalletId()).get(0);
    assertThat(agentWalletBefore.getId(), equalTo(agentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(agentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(agentWalletTransaction.getNote()));
    assertThat(agentWalletAfter.getBalance(), equalTo(agentWalletTransaction.getBalance()));

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getWalletId()).get(0);
    assertThat(subAgentWalletBefore.getId(), equalTo(subAgentWalletTransaction.getWalletId()));
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(subAgentWalletTransaction.getOperationType()));
    assertThat(note, equalTo(subAgentWalletTransaction.getNote()));
    assertThat(subAgentWalletAfter.getBalance(), equalTo(subAgentWalletTransaction.getBalance()));

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(note));
    assertThat(agentTransaction.getBalance(), equalTo(currentSubAgentBalance + amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.ADD_SALDO));
  }


  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testDepositCreditsToAgentFromAgent() throws Exception {
    Long startCreditBalanceForAgent = 200L;
    Long startCreditBalanceForSubAgent = 300L;
    Long amount = 100L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_3);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);

    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    updateBalanceForWallet(subAgent.getCreditWalletId(), startCreditBalanceForSubAgent);

    AgentCreditOperation operation = new AgentCreditOperation();
    operation.setAgentId(subAgent.getId());
    operation.setAmount(amount);

    mockMvc.perform(post("/api/agent/credits/deposit")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(operation)))
        .andExpect(status().isAccepted());

    Long endCreditBalanceForAgent = startCreditBalanceForAgent - amount;
    Long endCreditBalanceForSubAgent = startCreditBalanceForSubAgent + amount;

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(agentWalletTransaction.getOperationType()));
    checkBalanceInWallet(agent.getCreditWalletId(), endCreditBalanceForAgent,
        agentWalletTransaction);

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(subAgentWalletTransaction.getOperationType()));
    checkBalanceInWallet(subAgent.getCreditWalletId(), endCreditBalanceForSubAgent,
        subAgentWalletTransaction);

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(""));
    assertThat(agentTransaction.getBalance(), equalTo(startCreditBalanceForSubAgent + amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.DEPOSIT));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testDepositCreditsToAgentFromOperator() throws Exception {
    Long startCreditBalanceForOperator = 200L;
    Long startCreditBalanceForSubAgent = 300L;
    Long amount = 100L;

    Agent operator = agentRepository.findByUserName(PREDEFINED_OPERATOR);
    updateBalanceForWallet(operator.getCreditWalletId(), startCreditBalanceForOperator);

    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    updateBalanceForWallet(subAgent.getCreditWalletId(), startCreditBalanceForSubAgent);

    AgentCreditOperation operation = new AgentCreditOperation();
    operation.setAgentId(subAgent.getId());
    operation.setAmount(amount);

    mockMvc.perform(post("/api/agent/credits/deposit")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(operation)))
        .andExpect(status().isAccepted());

    Long endCreditBalanceForOperator = startCreditBalanceForOperator - amount;
    Long endCreditBalanceForSubAgent = startCreditBalanceForSubAgent + amount;

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        operator.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(agentWalletTransaction.getOperationType()));
    checkBalanceInWallet(operator.getCreditWalletId(), endCreditBalanceForOperator,
        agentWalletTransaction);

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(subAgentWalletTransaction.getOperationType()));
    checkBalanceInWallet(subAgent.getCreditWalletId(), endCreditBalanceForSubAgent,
        subAgentWalletTransaction);
  }


  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testWithdrawCreditsToAgentFromAgent() throws Exception {
    Long startCreditBalanceForAgent = 200L;
    Long startCreditBalanceForSubAgent = 300L;
    Long amount = 100L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_3);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);

    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    updateBalanceForWallet(subAgent.getCreditWalletId(), startCreditBalanceForSubAgent);

    AgentCreditOperation operation = new AgentCreditOperation();
    operation.setAgentId(subAgent.getId());
    operation.setAmount(amount);

    mockMvc.perform(post("/api/agent/credits/withdraw")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(operation)))
        .andExpect(status().isAccepted());

    Long endCreditBalanceForAgent = startCreditBalanceForAgent + amount;
    Long endCreditBalanceForSubAgent = startCreditBalanceForSubAgent - amount;

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        agent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(agentWalletTransaction.getOperationType()));
    checkBalanceInWallet(agent.getCreditWalletId(), endCreditBalanceForAgent,
        agentWalletTransaction);

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(subAgentWalletTransaction.getOperationType()));
    checkBalanceInWallet(subAgent.getCreditWalletId(), endCreditBalanceForSubAgent,
        subAgentWalletTransaction);

    List<AgentTransaction> agentTransactions = agentTransactionRepository.findAll();
    assertThat(agentTransactions, hasSize(1));
    AgentTransaction agentTransaction = agentTransactions.get(0);
    assertThat(agentTransaction.getAmount(), equalTo(amount));
    assertThat(agentTransaction.getSourceUser().getId(), equalTo(agent.getUser().getId()));
    assertThat(agentTransaction.getTargetUser().getId(), equalTo(subAgent.getUser().getId()));
    assertThat(agentTransaction.getNote(), equalTo(""));
    assertThat(agentTransaction.getBalance(), equalTo(startCreditBalanceForSubAgent - amount));
    assertThat(agentTransaction.getOperationType(), equalTo(AgentTransactionType.WITHDRAWAL));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testDepositCreditsToOperatorFromAgent() throws Exception {
    Long startCreditBalanceForOperator = 200L;
    Long startCreditBalanceForSubAgent = 300L;
    Long amount = 100L;

    Agent operator = agentRepository.findByUserName(PREDEFINED_OPERATOR);
    updateBalanceForWallet(operator.getCreditWalletId(), startCreditBalanceForOperator);

    Agent subAgent = agentRepository.findByUserName(PREDEFINED_SUBAGENT_3_1_1);
    updateBalanceForWallet(subAgent.getCreditWalletId(), startCreditBalanceForSubAgent);

    AgentCreditOperation operation = new AgentCreditOperation();
    operation.setAgentId(subAgent.getId());
    operation.setAmount(amount);

    mockMvc.perform(post("/api/agent/credits/withdraw")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(operation)))
        .andExpect(status().isAccepted());

    Long endCreditBalanceForOperator = startCreditBalanceForOperator + amount;
    Long endCreditBalanceForSubAgent = startCreditBalanceForSubAgent - amount;

    WalletTransaction agentWalletTransaction = walletTransactionRepository.findByWalletId(
        operator.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(agentWalletTransaction.getAmount()));
    assertThat(CREDIT, equalTo(agentWalletTransaction.getOperationType()));
    checkBalanceInWallet(operator.getCreditWalletId(), endCreditBalanceForOperator,
        agentWalletTransaction);

    WalletTransaction subAgentWalletTransaction = walletTransactionRepository.findByWalletId(
        subAgent.getCreditWalletId()).get(0);
    assertThat(amount, equalTo(subAgentWalletTransaction.getAmount()));
    assertThat(DEBIT, equalTo(subAgentWalletTransaction.getOperationType()));
    checkBalanceInWallet(subAgent.getCreditWalletId(), endCreditBalanceForSubAgent,
        subAgentWalletTransaction);
  }

  private CreateAgentDto getCreateAgentDto() {
    CreateAgentDto dto = new CreateAgentDto();
    dto.setUserName(AGENT_USER_NAME);
    dto.setFullName(AGENT_FULL_NAME);
    dto.setPassword(AGENT_PASSWORD);
    dto.setCommission(AGENT_COMMISSION);
    dto.setCommissionCasino(AGENT_COMMISSION);
    dto.setCommissionSlots(AGENT_COMMISSION);
    dto.setCommissionSports(AGENT_COMMISSION);
    dto.setCanHaveSubAgents(AGENT_CAN_HAVE_SUB_AGENTS);
    return dto;
  }

  private UpdateAgentDto getUpdateAgentDto() {
    UpdateAgentDto updateAgentDto = new UpdateAgentDto();
    updateAgentDto.setEmail(AGENT_EMAIL_UPDATE);
    updateAgentDto.setPhoneNumber(AGENT_PHONE_NUMBER_UPDATE);
    updateAgentDto.setCommission(AGENT_COMMISSION_UPDATE);
    updateAgentDto.setCommissionCasino(AGENT_COMMISSION_UPDATE);
    updateAgentDto.setCommissionSlots(AGENT_COMMISSION_UPDATE);
    updateAgentDto.setCommissionSports(AGENT_COMMISSION_UPDATE);
    updateAgentDto.setCanHaveSubAgents(AGENT_CAN_HAVE_SUB_AGENTS_UPDATE);
    updateAgentDto.setFullName(AGENT_FULL_NAME_UPDATE);
    updateAgentDto.setUserName(AGENT_USER_NAME_UPDATE);
    return updateAgentDto;
  }

  private Agent getAgentForCurrentUser() {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    return agentRepository.getEntity(appUserDetails.getId());
  }
}
