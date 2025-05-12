package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_3;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_4;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.UserTransactionType;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentRestTransactionType;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentTransactionsRequest;
import com.betmotion.agentsmanagement.rest.dto.reports.PlayerCreditTransactionType;
import com.betmotion.agentsmanagement.rest.dto.reports.PlayerTransactionsRequest;
import com.google.common.collect.Lists;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = {CLEAN_DB_SQL, AGENT_DATA_SQL})
class ReportControllerTest extends AbstractIntegrationTest {


  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testGetPlayerTransactions() throws Exception {
    String note = "ExampleOfNote";
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    userTransactionService.create(agent, player.getId(), UserTransactionType.DEPOSIT,
        100L, 100L, note, LocalDateTime.now(), null);
    PlayerTransactionsRequest request = new PlayerTransactionsRequest();
    request.setTransactionTypes(Lists.newArrayList(PlayerCreditTransactionType.ADD_CREDITS));
    mockMvc.perform(post("/api/reports/players/transactions")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3L))
        .andExpect(jsonPath("$.totalPages").value(1L))
        .andExpect(jsonPath("$.totalElements").value(1L))
        .andExpect(jsonPath("$.data.length()").value(1L))
        .andExpect(jsonPath("$.data[0].length()").value(7L))
        .andExpect(jsonPath("$.data[0].playerId").isNotEmpty())
        .andExpect(jsonPath("$.data[0].playerName").value(PREDEFINED_PLAYER_3_1_1))
        .andExpect(jsonPath("$.data[0].amount").value(100L))
        .andExpect(jsonPath("$.data[0].note").value(note))
        .andExpect(jsonPath("$.data[0].balance").value(100L))
        .andExpect(jsonPath("$.data[0].transactionDate").isNotEmpty())
        .andExpect(jsonPath("$.data[0].transactionType")
            .value("ADD_CREDITS"));
  }

  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  @Test
  void testGetAgentTransactions() throws Exception {
    Long amount = 10000L;
    Long balanceAfterOperation = 100L;
    String note = "ExampleOfNote";
    Agent sourceAgent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_3);
    Agent targetAgent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);

    agentTransactionService.createTransaction(sourceAgent.getUser(), targetAgent.getUser(), amount,
        AgentTransactionType.ADD_SALDO, note, balanceAfterOperation, null);

    AgentTransactionsRequest request = new AgentTransactionsRequest();
    request.setTransactionTypes(Lists.newArrayList(AgentRestTransactionType.ADD_SALDO));
    request.setAgentIds(Collections.singletonList(targetAgent.getId()));
    mockMvc.perform(post("/api/reports/agents/transactions")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3L))
        .andExpect(jsonPath("$.totalPages").value(1L))
        .andExpect(jsonPath("$.totalElements").value(1L))
        .andExpect(jsonPath("$.data.length()").value(1L))
        .andExpect(jsonPath("$.data[0].length()").value(6L))
        .andExpect(jsonPath("$.data[0].agentName").value(PREDEFINED_AGENT_WITH_SUBAGENTS_4))
        .andExpect(jsonPath("$.data[0].amount").value(amount))
        .andExpect(jsonPath("$.data[0].note").value(note))
        .andExpect(jsonPath("$.data[0].balance").value(balanceAfterOperation))
        .andExpect(jsonPath("$.data[0].transactionDate").isNotEmpty())
        .andExpect(jsonPath("$.data[0].transactionType").value("ADD_SALDO"));
  }

}
