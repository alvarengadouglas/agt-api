package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.LENGTH;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_4;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_OPERATOR;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.rest.dto.user.UserRole;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = {CLEAN_DB_SQL, AGENT_DATA_SQL})
class AuthControllerTest extends AbstractIntegrationTest {

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testGetMeForAgent() throws Exception {
    Long newBalance = 200L;
    updateBalanceForCurrentAgent(newBalance);
    mockMvc.perform(get("/api/auth/me")
        ).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.userName").value(PREDEFINED_AGENT_WITH_SUBAGENTS_4))
        .andExpect(jsonPath("$.role").value(UserRole.AGENT.name()))
        .andExpect(jsonPath("$.agentCode").isNotEmpty())
        .andExpect(jsonPath("$.balance").value(newBalance))
        .andExpect(jsonPath("$.credits").value(4000L))
        .andExpect(jsonPath(LENGTH).value(5));
  }


  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testGetMeForOperator() throws Exception {
    Long newBalance = 200L;
    updateBalanceForCurrentAgent(newBalance);
    mockMvc.perform(get("/api/auth/me")
        ).andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.userName").value(PREDEFINED_OPERATOR))
        .andExpect(jsonPath("$.role").value(UserRole.OPERATOR.name()))
        .andExpect(jsonPath("$.agentCode").value("OPERATOR"))
        .andExpect(jsonPath("$.balance").value(newBalance))
        .andExpect(jsonPath("$.credits").value(200000L))
        .andExpect(jsonPath(LENGTH).value(5));
  }

  private void updateBalanceForCurrentAgent(Long currentBalance) {
    AppUserDetails userDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Integer agentId = userDetails.getId();
    Agent agent = agentRepository.findById(agentId).get();
    agent.setBalance(currentBalance);
    agentRepository.save(agent);
  }
}
