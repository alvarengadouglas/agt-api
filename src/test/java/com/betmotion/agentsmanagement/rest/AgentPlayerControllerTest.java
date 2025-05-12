package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.domain.UserTransactionType.DEPOSIT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.PAYMENT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.PAYOUT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.WITHDRAWAL;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.CREDIT;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.DEBIT;
import static com.betmotion.agentsmanagement.platform.api.dto.UserStatus.BLOCKED;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_DATA_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.AGENT_ID;
import static com.betmotion.agentsmanagement.utils.TestConstants.CLEAN_DB_SQL;
import static com.betmotion.agentsmanagement.utils.TestConstants.CREATED_ON;
import static com.betmotion.agentsmanagement.utils.TestConstants.EMAIL;
import static com.betmotion.agentsmanagement.utils.TestConstants.ID;
import static com.betmotion.agentsmanagement.utils.TestConstants.LAST_LOGIN;
import static com.betmotion.agentsmanagement.utils.TestConstants.MAX_ATTEMPTS;
import static com.betmotion.agentsmanagement.utils.TestConstants.PHONE;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_EMAIL;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_FIRST_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_LAST_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_PASSWORD;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_PHONE;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_PHONE_COUNTRY_ID;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_RECEIVE_EMAIL;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_ROLE;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_STATUS;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_TEST_USER;
import static com.betmotion.agentsmanagement.utils.TestConstants.PLAYER_USER_NAME;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_3;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_AGENT_WITH_SUBAGENTS_4;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_OPERATOR;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_3_1_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_PLAYER_4_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.PREDEFINED_SUBAGENT_3_1_1;
import static com.betmotion.agentsmanagement.utils.TestConstants.RECEIVE_EMAIL;
import static com.betmotion.agentsmanagement.utils.TestConstants.ROLE;
import static com.betmotion.agentsmanagement.utils.TestConstants.STATUS;
import static com.betmotion.agentsmanagement.utils.TestConstants.TEST_USER;
import static com.betmotion.agentsmanagement.utils.TestConstants.USER_NAME;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.util.stream.LongStream.range;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.betmotion.agentsmanagement.AbstractIntegrationTest;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.AgentPlayer;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.PlayerWallet;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.domain.UserTransaction;
import com.betmotion.agentsmanagement.domain.UserTransactionType;
import com.betmotion.agentsmanagement.domain.WalletTransaction;
import com.betmotion.agentsmanagement.platform.api.dto.ChangePasswordRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.ChangeStatusRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.ChangeStatusResponseDto;
import com.betmotion.agentsmanagement.platform.api.dto.DepositUserRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.DepositUserResponseDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformApiErrorDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformWalletDto;
import com.betmotion.agentsmanagement.platform.api.dto.RegisterUserRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.RegisterUserResponseDto;
import com.betmotion.agentsmanagement.platform.api.dto.WithdrawalUserRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.WithdrawalUserResponseDto;
import com.betmotion.agentsmanagement.rest.dto.CollectPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.agent.DepositAgentUserBalanceOperation;
import com.betmotion.agentsmanagement.rest.dto.agent.WithdrawalAgentUserBalanceOperation;
import com.betmotion.agentsmanagement.rest.dto.player.ChangePasswordPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.player.CreatePlayerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = { CLEAN_DB_SQL, AGENT_DATA_SQL })
class AgentPlayerControllerTest extends AbstractIntegrationTest {

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testAddCreditsToPlayerForFree() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long platformBalanceAfterTransaction = 230L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    updateBalanceForWallet(agent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);

    Long amount = 30L;
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_4_1);

    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());
    Long startUserBalance = playerWallet.getBalance();
    Long startPlatFormBalance = playerWallet.getPlatformBalance();

    range(1, MAX_ATTEMPTS).forEach(i -> {
      DepositUserRequestDto requestDto = new DepositUserRequestDto();
      requestDto.setAmount(amount);
      requestDto.setAgent(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
      requestDto.setUserName(player.getUserName());
      requestDto.setRemoteId(i);

      DepositUserResponseDto response = new DepositUserResponseDto();
      response.setUserName(player.getUserName());
      response.setTransactionId(6L);
      PlatformWalletDto platformWalletDto = new PlatformWalletDto();
      platformWalletDto.setBalance(platformBalanceAfterTransaction);
      response.setWallet(platformWalletDto);

      try {
        stubFor(WireMock.post(urlEqualTo("/agent/deposit/apply.do"))
            .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
            .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

    });
    DepositAgentUserBalanceOperation dto = new DepositAgentUserBalanceOperation();
    dto.setPlayerId(player.getId());
    dto.setAmount(amount);
    dto.setForFree(Boolean.TRUE);
    mockMvc.perform(post("/api/agent/player/addCredits")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
    Long newBalanceForAgent = startBalanceForAgent;
    Long newCreditsBalanceForAgent = startCreditBalanceForAgent - amount;
    checkBalanceInWallet(agent.getWalletId(), newBalanceForAgent);
    checkBalanceInWallet(agent.getCreditWalletId(), newCreditsBalanceForAgent);

    assertThat(userTransactionRepository.count(), equalTo(1L));
    UserTransaction userTransaction = userTransactionRepository.findAll().get(0);
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(DEPOSIT));
    assertThat(userTransaction.getBalance(), equalTo(startUserBalance + amount));

    assertThat(walletTransactionRepository.findByWalletId(agent.getWalletId()), hasSize(0));
    List<WalletTransaction> operationBtCreditsWallet = walletTransactionRepository.findByWalletId(
        agent.getCreditWalletId());

    assertThat(operationBtCreditsWallet, hasSize(1));
    WalletTransaction walletTransaction = operationBtCreditsWallet.get(0);
    assertThat(walletTransaction.getOperationType(), equalTo(DEBIT));
    assertThat(walletTransaction.getAmount(), equalTo(amount));

    PlayerWallet playerWalletAfterOperation = playerWalletRepository
        .findAndLockByPlayerId(player.getId());
    Long endUserBalance = playerWalletAfterOperation.getBalance();
    Long endPlatFormBalance = playerWalletAfterOperation.getPlatformBalance();

    assertThat(endUserBalance, equalTo(startUserBalance + amount));
    assertThat(endPlatFormBalance, equalTo(startPlatFormBalance + amount));

  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testAddCreditsToPlayerByOperator() throws Exception {
    Long amount = 30L;
    DepositAgentUserBalanceOperation dto = new DepositAgentUserBalanceOperation();
    dto.setPlayerId(1);
    dto.setAmount(amount);
    dto.setForFree(Boolean.TRUE);
    mockMvc.perform(post("/api/agent/player/addCredits")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testAddCreditsToPlayerNotForFree() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long platformBalanceAfterTransaction = 700L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    updateBalanceForWallet(agent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);

    Long amount = 30L;
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_4_1);

    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());
    Long startUserBalance = playerWallet.getBalance();
    Long startPlatFormBalance = playerWallet.getPlatformBalance();

    range(1, MAX_ATTEMPTS).forEach(l -> {
      DepositUserRequestDto requestDto = new DepositUserRequestDto();
      requestDto.setAmount(amount);
      requestDto.setAgent(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
      requestDto.setUserName(player.getUserName());
      requestDto.setRemoteId(l);

      DepositUserResponseDto response = new DepositUserResponseDto();
      response.setUserName(player.getUserName());
      response.setTransactionId(3L);
      PlatformWalletDto platformWalletDto = new PlatformWalletDto();
      platformWalletDto.setBalance(platformBalanceAfterTransaction);
      response.setWallet(platformWalletDto);

      try {
        stubFor(WireMock.post(urlEqualTo("/agent/deposit/apply.do"))
            .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
            .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

    });

    DepositAgentUserBalanceOperation dto = new DepositAgentUserBalanceOperation();
    dto.setPlayerId(player.getId());
    dto.setAmount(amount);
    dto.setForFree(Boolean.FALSE);
    mockMvc.perform(post("/api/agent/player/addCredits")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
    Long newBalanceForAgent = startBalanceForAgent + amount;
    checkBalanceInWallet(agent.getWalletId(), newBalanceForAgent);
    Long newCreditBalanceForAgent = startCreditBalanceForAgent - amount;
    checkBalanceInWallet(agent.getCreditWalletId(), newCreditBalanceForAgent);

    List<UserTransaction> allUserTransactions = userTransactionRepository.findAll();
    assertThat(allUserTransactions, hasSize(2));

    UserTransaction userTransaction = allUserTransactions.stream()
        .filter(item -> item.getOperationType().equals(DEPOSIT)).findFirst().get();
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(DEPOSIT));
    assertThat(userTransaction.getBalance(), equalTo(startUserBalance + amount));

    userTransaction = allUserTransactions.stream()
        .filter(item -> item.getOperationType().equals(PAYMENT)).findFirst().get();
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(PAYMENT));
    assertThat(userTransaction.getBalance(), equalTo(startUserBalance));

    List<WalletTransaction> operationsByMoneyWallet = walletTransactionRepository.findByWalletId(
        agent.getWalletId());
    assertThat(operationsByMoneyWallet, hasSize(1));
    WalletTransaction walletTransaction = operationsByMoneyWallet.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getOperationType(), equalTo(CREDIT));
    assertThat(walletTransaction.getWalletId(), equalTo(agent.getWalletId()));

    List<WalletTransaction> operationsByCreditWallet = walletTransactionRepository.findByWalletId(
        agent.getCreditWalletId());
    assertThat(operationsByMoneyWallet, hasSize(1));
    walletTransaction = operationsByCreditWallet.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getOperationType(), equalTo(DEBIT));
    assertThat(walletTransaction.getWalletId(), equalTo(agent.getCreditsWallet().getId()));

    PlayerWallet playerWalletAfterOperation = playerWalletRepository
        .findAndLockByPlayerId(player.getId());
    Long endUserBalance = playerWalletAfterOperation.getBalance();
    Long endPlatFormBalance = playerWalletAfterOperation.getPlatformBalance();

    assertThat(endUserBalance, equalTo(startUserBalance));
    assertThat(endPlatFormBalance, equalTo(startPlatFormBalance + amount));

  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testWithdrawCreditsForFreeFromUser() throws Exception {
    Long startCreditBalanceForAgent = 100L;
    Long startPlatformBalanceForPlayer = 500L;
    Long startPlayerAgentBalance = 1500L;
    Long startBalanceForAgent = 300L;
    Long platformBalanceAfterTransaction = 500L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);
    updateBalanceForWallet(agent.getWalletId(), startBalanceForAgent);

    Long amount = 30L;
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(player.getId());
    Agent playerCreator = agentRepository.getEntity(agentPlayer.getAgentId());
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());
    playerWallet.setPlatformBalance(startPlatformBalanceForPlayer);
    playerWallet.setBalance(startPlayerAgentBalance);
    playerWalletRepository.save(playerWallet);
    User creatorUser = userRepository.getEntity(playerCreator.getUserId());

    range(1, MAX_ATTEMPTS).forEach(l -> {
      WithdrawalUserRequestDto requestDto = new WithdrawalUserRequestDto();
      requestDto.setAmount(amount);
      requestDto.setAgent(creatorUser.getUserName());
      requestDto.setUserName(player.getUserName());
      requestDto.setRemoteId(l);

      WithdrawalUserResponseDto response = new WithdrawalUserResponseDto();
      response.setUserName(player.getUserName());
      response.setTransactionId(1L);
      PlatformWalletDto platformWalletDto = new PlatformWalletDto();
      platformWalletDto.setBalance(platformBalanceAfterTransaction);
      response.setWallet(platformWalletDto);

      try {
        stubFor(WireMock.post(urlEqualTo("/agent/withdrawal/apply.do"))
            .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
            .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    WithdrawalAgentUserBalanceOperation dto = new WithdrawalAgentUserBalanceOperation();
    dto.setPlayerId(player.getId());
    dto.setAmount(amount);
    dto.setForFree(Boolean.TRUE);

    mockMvc.perform(post("/api/agent/player/withdrawCredits")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());

    Long newCreditBalanceForAgent = startCreditBalanceForAgent + amount;
    Long newPlatformBalanceForPlayer = startPlatformBalanceForPlayer - amount;
    Long newPlayerAgentBalance = startPlayerAgentBalance - amount;
    Long newBalanceForAgent = startBalanceForAgent;
    checkBalanceInWallet(agent.getCreditWalletId(), newCreditBalanceForAgent);
    checkBalanceInWallet(agent.getWalletId(), newBalanceForAgent);

    PlayerWallet playWalletAfterOperation = playerWalletRepository.findAndLockByPlayerId(
        player.getId());
    assertThat(playWalletAfterOperation.getPlatformBalance(), equalTo(newPlatformBalanceForPlayer));
    assertThat(playWalletAfterOperation.getBalance(), equalTo(newPlayerAgentBalance));

    assertThat(userTransactionRepository.count(), equalTo(1L));
    UserTransaction userTransaction = userTransactionRepository.findAll().get(0);
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(UserTransactionType.WITHDRAWAL));
    assertThat(userTransaction.getBalance(), equalTo(startPlayerAgentBalance - amount));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testWithdrawCreditsNotForFreeFromUser() throws Exception {
    Long startCreditBalanceForAgent = 100L;
    Long startPlatformBalanceForPlayer = 500L;
    Long startPlayerAgentBalance = 1500L;
    Long startBalanceForAgent = 300L;
    Long platformBalanceAfterTransaction = 1000L;

    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    updateBalanceForWallet(agent.getCreditWalletId(), startCreditBalanceForAgent);
    updateBalanceForWallet(agent.getWalletId(), startBalanceForAgent);

    Long amount = 30L;
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());
    playerWallet.setPlatformBalance(startPlatformBalanceForPlayer);
    playerWallet.setBalance(startPlayerAgentBalance);
    playerWalletRepository.save(playerWallet);

    range(1, MAX_ATTEMPTS).forEach(l -> {
      WithdrawalUserRequestDto requestDto = new WithdrawalUserRequestDto();
      requestDto.setAmount(amount);
      requestDto.setAgent(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
      requestDto.setUserName(player.getUserName());
      requestDto.setRemoteId(l);

      WithdrawalUserResponseDto response = new WithdrawalUserResponseDto();
      response.setUserName(player.getUserName());
      response.setTransactionId(1L);
      PlatformWalletDto platformWalletDto = new PlatformWalletDto();
      platformWalletDto.setBalance(platformBalanceAfterTransaction);
      response.setWallet(platformWalletDto);

      try {
        stubFor(WireMock.post(urlEqualTo("/agent/withdrawal/apply.do"))
            .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
            .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
            .willReturn(aResponse()
                .withStatus(OK.value())
                .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(objectMapper.writeValueAsString(response))));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    });

    WithdrawalAgentUserBalanceOperation dto = new WithdrawalAgentUserBalanceOperation();
    dto.setPlayerId(player.getId());
    dto.setAmount(amount);
    dto.setForFree(Boolean.FALSE);

    mockMvc.perform(post("/api/agent/player/withdrawCredits")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());

    Long newCreditBalanceForAgent = startCreditBalanceForAgent + amount;
    Long newPlatformBalanceForPlayer = startPlatformBalanceForPlayer - amount;
    Long newPlayerAgentBalance = startPlayerAgentBalance;
    Long newBalanceForAgent = startBalanceForAgent - amount;

    checkBalanceInWallet(agent.getCreditWalletId(), newCreditBalanceForAgent);
    checkBalanceInWallet(agent.getWalletId(), newBalanceForAgent);

    PlayerWallet playWalletAfterOperation = playerWalletRepository.findAndLockByPlayerId(
        player.getId());
    assertThat(playWalletAfterOperation.getPlatformBalance(), equalTo(newPlatformBalanceForPlayer));
    assertThat(playWalletAfterOperation.getBalance(), equalTo(newPlayerAgentBalance));

    List<UserTransaction> userTransactions = userTransactionRepository.findAll();

    assertThat(userTransactions, hasSize(2));

    UserTransaction userTransaction = userTransactions.stream()
        .filter(item -> item.getOperationType().equals(WITHDRAWAL))
        .findFirst()
        .get();
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(UserTransactionType.WITHDRAWAL));
    assertThat(userTransaction.getBalance(), equalTo(startPlayerAgentBalance - amount));

    userTransaction = userTransactions.stream()
        .filter(item -> item.getOperationType().equals(PAYOUT))
        .findFirst()
        .get();
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getUserid(), equalTo(agent.getUserId()));
    assertThat(userTransaction.getOperationType(), equalTo(UserTransactionType.PAYOUT));
    assertThat(userTransaction.getBalance(), equalTo(startPlayerAgentBalance));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testCreatePlayer() throws Exception {
    Integer userIdInPlatform = 100;
    RegisterUserResponseDto response = new RegisterUserResponseDto();
    response.setId(userIdInPlatform);
    response.setUserName(PLAYER_USER_NAME);
    CreatePlayerDto playerDto = createPlayerDto();
    RegisterUserRequestDto requestDto = new RegisterUserRequestDto();
    requestDto.setAgent(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    requestDto.setEmail(playerDto.getEmail());
    requestDto.setPassword(playerDto.getPassword());
    requestDto.setUserName(playerDto.getUserName());
    requestDto.setFirstName(playerDto.getFirstName());
    requestDto.setLastName(playerDto.getLastName());
    requestDto.setCellPhone(playerDto.getPhoneNumber());
    requestDto.setCellPhoneCountryId(playerDto.getCountryId());
    stubFor(WireMock.post(urlEqualTo("/agent/user/register.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(requestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(response))));

    mockMvc.perform(post("/api/agent/player")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(playerDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath(ID).isNotEmpty())
        .andExpect(jsonPath(CREATED_ON).isNotEmpty())
        .andExpect(jsonPath(EMAIL).value(PLAYER_EMAIL))
        .andExpect(jsonPath(LAST_LOGIN).isNotEmpty())
        .andExpect(jsonPath(PHONE).value(PLAYER_PHONE))
        .andExpect(jsonPath(RECEIVE_EMAIL).value(PLAYER_RECEIVE_EMAIL))
        .andExpect(jsonPath(ROLE).value(PLAYER_ROLE))
        .andExpect(jsonPath(STATUS).value(PLAYER_STATUS))
        .andExpect(jsonPath(TEST_USER).value(PLAYER_TEST_USER))
        .andExpect(jsonPath(USER_NAME).value(PLAYER_USER_NAME))
        .andExpect(jsonPath(AGENT_ID).isNotEmpty());

    Player player = playerRepository.findByPlatformId(userIdInPlatform);
    assertThat(player, notNullValue());
    assertThat(player.getPlatformId(), equalTo(userIdInPlatform));
    assertThat(player.getUserName(), equalTo(PLAYER_USER_NAME));

    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(player.getId());
    assertThat(agentPlayer, notNullValue());
    Agent agent = agentRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    assertThat(agentPlayer.getAgentId(), equalTo(agent.getId()));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testCreatePlayerWhenUserAlreadyExists() throws Exception {
    PlatformApiErrorDto response = new PlatformApiErrorDto();
    String messageCode = "PT01";
    response.setCode(messageCode);
    String messageText = "Error while creating player in the platform";
    response.setMessage(messageText);
    CreatePlayerDto playerDto = createPlayerDto();
    RegisterUserRequestDto requestDto = new RegisterUserRequestDto();
    requestDto.setAgent(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    requestDto.setEmail(playerDto.getEmail());
    requestDto.setPassword(playerDto.getPassword());
    requestDto.setUserName(playerDto.getUserName());
    requestDto.setFirstName(playerDto.getFirstName());
    requestDto.setLastName(playerDto.getLastName());
    requestDto.setCellPhone(playerDto.getPhoneNumber());
    requestDto.setCellPhoneCountryId(playerDto.getCountryId());
    stubFor(WireMock.post(urlEqualTo("/agent/user/register.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .willReturn(aResponse()
            .withStatus(INTERNAL_SERVER_ERROR.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(response))));

    mockMvc.perform(post("/api/agent/player")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(playerDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.globalErrors.length()").value(1L))
        .andExpect(jsonPath("$.globalErrors[0]").value(messageText));

    assertThat(playerRepository.findByUserName(playerDto.getUserName()), nullValue());
  }

  private CreatePlayerDto createPlayerDto() {
    CreatePlayerDto dto = new CreatePlayerDto();
    dto.setEmail(PLAYER_EMAIL);
    dto.setPassword(PLAYER_PASSWORD);
    dto.setUserName(PLAYER_USER_NAME);
    dto.setFirstName(PLAYER_FIRST_NAME);
    dto.setLastName(PLAYER_LAST_NAME);
    dto.setPhoneNumber(PLAYER_PHONE);
    dto.setCountryId(PLAYER_PHONE_COUNTRY_ID);
    return dto;
  }

  @Test
  @WithUserDetails(value = PREDEFINED_SUBAGENT_3_1_1)
  void blockPlayer() throws Exception {
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1_1);
    ChangeStatusResponseDto statusResponseDto = new ChangeStatusResponseDto();
    statusResponseDto.setUserName(player.getUserName());
    ChangeStatusRequestDto statusRequestDto = new ChangeStatusRequestDto();
    statusRequestDto.setUserName(player.getUserName());
    statusRequestDto.setStatus(BLOCKED);
    stubFor(WireMock.post(urlEqualTo("/agent/user/change-status.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(statusRequestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(statusResponseDto))));
    int playerId = player.getId();
    mockMvc.perform(put("/api/agent/player/" + playerId + "/block"))
        .andExpect(status().isAccepted());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testBlockPlayerThrowExceptionNotControlledByThisAgent() throws Exception {

    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    ChangeStatusResponseDto statusResponseDto = new ChangeStatusResponseDto();
    statusResponseDto.setUserName(player.getUserName());
    ChangeStatusRequestDto statusRequestDto = new ChangeStatusRequestDto();
    statusRequestDto.setUserName(player.getUserName());
    statusRequestDto.setStatus(BLOCKED);
    stubFor(WireMock.post(urlEqualTo("/agent/user/change-status.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(statusRequestDto)))
        .willReturn(aResponse()
            .withStatus(OK.value())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .withBody(objectMapper.writeValueAsString(statusResponseDto))));
    int playerId = player.getId();
    mockMvc.perform(put("/api/agent/player/" + playerId + "/block"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testBlockPlayerThrowExceptionNotValidPlayerId() throws Exception {
    mockMvc.perform(put("/api/agent/player/" + Integer.MAX_VALUE + "/block"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testChangePasswordForPlayer() throws Exception {
    String newPassword = "AAAA";

    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_4_1);
    ChangePasswordPlayerDto requestDto = new ChangePasswordPlayerDto();
    requestDto.setPlayerId(player.getId());
    requestDto.setPassword(newPassword);

    User userForAgent = userRepository.findByUserName(PREDEFINED_AGENT_WITH_SUBAGENTS_4);
    ChangePasswordRequestDto platfromApiRequestDto = new ChangePasswordRequestDto();
    platfromApiRequestDto.setPassword(newPassword);
    platfromApiRequestDto.setAgent(userForAgent.getUserName());
    platfromApiRequestDto.setUserName(player.getUserName());

    stubFor(WireMock.post(urlEqualTo("/agent/user/change-password.do"))
        .withHeader(CONTENT_TYPE, new EqualToPattern(APPLICATION_JSON_VALUE))
        .withRequestBody(new EqualToPattern(objectMapper.writeValueAsString(platfromApiRequestDto)))
        .willReturn(aResponse()
            .withStatus(ACCEPTED.value())));

    mockMvc.perform(post("/api/agent/player/change-password")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_3)
  void testCollectFromPlayerByAgent() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long startBalanceForPlayerAgent = 300L;
    Long amount = 70L;
    String note = "AAA";
    Agent currentAgent = agentRepository.findByUserName(
        userProvider.getCurrentUserDetails().getUsername());
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    updateBalanceForWallet(currentAgent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(currentAgent.getCreditWalletId(), startCreditBalanceForAgent);
    updatePlayerBalanceForAgent(player.getId(), startBalanceForPlayerAgent);
    CollectPlayerDto collectPlayerDto = new CollectPlayerDto();
    collectPlayerDto.setAmount(amount);
    collectPlayerDto.setPlayerId(player.getId());
    collectPlayerDto.setNote(note);

    mockMvc.perform(post("/api/agent/player/collect")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(collectPlayerDto)))
        .andExpect(status().isAccepted());

    Long newBalanceForAgent = startBalanceForAgent + amount;
    Long newCreditBalanceForAgent = startCreditBalanceForAgent;
    Long newAgentPlayerBalance = startBalanceForPlayerAgent - amount;
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent);
    checkBalanceInWallet(currentAgent.getCreditWalletId(), newCreditBalanceForAgent);
    checkPlayerAgentBalance(player.getId(), newAgentPlayerBalance);

    List<WalletTransaction> allTransactions = walletTransactionRepository.findAll();
    assertThat(allTransactions, hasSize(1));
    WalletTransaction walletTransaction = allTransactions.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getWalletId(), equalTo(currentAgent.getWalletId()));
    assertThat(walletTransaction.getOperationType(), equalTo(CREDIT));
    assertThat(walletTransaction.getNote(), equalTo(note));

    List<UserTransaction> userTransactions = userTransactionRepository.findAll();
    assertThat(userTransactions, hasSize(1));
    UserTransaction userTransaction = userTransactions.get(0);
    assertThat(userTransaction.getUserid(), equalTo(currentAgent.getUserId()));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getOperationType(), equalTo(PAYMENT));
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getBalance(), equalTo(startBalanceForPlayerAgent - amount));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testCollectFromPlayerByOperator() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long startBalanceForPlayerAgent = 300L;
    Long amount = 70L;
    String note = "AAA";
    Agent currentAgent = agentRepository.findByUserName(
        userProvider.getCurrentUserDetails().getUsername());
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    updateBalanceForWallet(currentAgent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(currentAgent.getCreditWalletId(), startCreditBalanceForAgent);
    updatePlayerBalanceForAgent(player.getId(), startBalanceForPlayerAgent);
    CollectPlayerDto collectPlayerDto = new CollectPlayerDto();
    collectPlayerDto.setAmount(amount);
    collectPlayerDto.setPlayerId(player.getId());
    collectPlayerDto.setNote(note);

    mockMvc.perform(post("/api/agent/player/collect")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(collectPlayerDto)))
        .andExpect(status().isAccepted());

    Long newBalanceForAgent = startBalanceForAgent + amount;
    Long newCreditBalanceForAgent = startCreditBalanceForAgent;
    Long newAgentPlayerBalance = startBalanceForPlayerAgent - amount;
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent);
    checkBalanceInWallet(currentAgent.getCreditWalletId(), newCreditBalanceForAgent);
    checkPlayerAgentBalance(player.getId(), newAgentPlayerBalance);

    List<WalletTransaction> allTransactions = walletTransactionRepository.findAll();
    assertThat(allTransactions, hasSize(1));
    WalletTransaction walletTransaction = allTransactions.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getWalletId(), equalTo(currentAgent.getWalletId()));
    assertThat(walletTransaction.getOperationType(), equalTo(CREDIT));
    assertThat(walletTransaction.getNote(), equalTo(note));

    List<UserTransaction> userTransactions = userTransactionRepository.findAll();
    assertThat(userTransactions, hasSize(1));
    UserTransaction userTransaction = userTransactions.get(0);
    assertThat(userTransaction.getUserid(), equalTo(currentAgent.getUserId()));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getOperationType(), equalTo(PAYMENT));
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getBalance(), equalTo(startBalanceForPlayerAgent - amount));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_AGENT_WITH_SUBAGENTS_4)
  void testPayoutToPlayerByAgent() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long startBalanceForPlayerAgent = 300L;
    Long amount = 70L;
    String note = "AAA";
    Agent currentAgent = agentRepository
        .findByUserName(userProvider.getCurrentUserDetails().getUsername());
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    updateBalanceForWallet(currentAgent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(currentAgent.getCreditWalletId(), startCreditBalanceForAgent);
    updatePlayerBalanceForAgent(player.getId(), startBalanceForPlayerAgent);
    CollectPlayerDto collectPlayerDto = new CollectPlayerDto();
    collectPlayerDto.setAmount(amount);
    collectPlayerDto.setPlayerId(player.getId());
    collectPlayerDto.setNote(note);

    mockMvc.perform(post("/api/agent/player/payout")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(collectPlayerDto)))
        .andExpect(status().isAccepted());

    Long newBalanceForAgent = startBalanceForAgent - amount;
    Long newCreditBalanceForAgent = startCreditBalanceForAgent;
    Long newAgentPlayerBalance = startBalanceForPlayerAgent + amount;
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent);
    checkBalanceInWallet(currentAgent.getCreditWalletId(), newCreditBalanceForAgent);
    checkPlayerAgentBalance(player.getId(), newAgentPlayerBalance);

    List<WalletTransaction> allTransactions = walletTransactionRepository.findAll();
    assertThat(allTransactions, hasSize(1));
    WalletTransaction walletTransaction = allTransactions.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getWalletId(), equalTo(currentAgent.getWalletId()));
    assertThat(walletTransaction.getOperationType(), equalTo(DEBIT));
    assertThat(walletTransaction.getNote(), equalTo(note));
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent, walletTransaction);

    List<UserTransaction> userTransactions = userTransactionRepository.findAll();
    assertThat(userTransactions, hasSize(1));
    UserTransaction userTransaction = userTransactions.get(0);
    assertThat(userTransaction.getUserid(), equalTo(currentAgent.getUserId()));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getOperationType(), equalTo(PAYOUT));
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getBalance(), equalTo(startBalanceForPlayerAgent + amount));
  }

  @Test
  @WithUserDetails(value = PREDEFINED_OPERATOR)
  void testPayoutToPlayerByOperator() throws Exception {
    Long startBalanceForAgent = 100L;
    Long startCreditBalanceForAgent = 200L;
    Long startBalanceForPlayerAgent = 300L;
    Long amount = 70L;
    String note = "AAA";
    Agent currentAgent = agentRepository
        .findByUserName(userProvider.getCurrentUserDetails().getUsername());
    Player player = playerRepository.findByUserName(PREDEFINED_PLAYER_3_1_1);
    updateBalanceForWallet(currentAgent.getWalletId(), startBalanceForAgent);
    updateBalanceForWallet(currentAgent.getCreditWalletId(), startCreditBalanceForAgent);
    updatePlayerBalanceForAgent(player.getId(), startBalanceForPlayerAgent);
    CollectPlayerDto collectPlayerDto = new CollectPlayerDto();
    collectPlayerDto.setAmount(amount);
    collectPlayerDto.setPlayerId(player.getId());
    collectPlayerDto.setNote(note);

    mockMvc.perform(post("/api/agent/player/payout")
        .contentType(APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(collectPlayerDto)))
        .andExpect(status().isAccepted());

    Long newBalanceForAgent = startBalanceForAgent - amount;
    Long newCreditBalanceForAgent = startCreditBalanceForAgent;
    Long newAgentPlayerBalance = startBalanceForPlayerAgent + amount;
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent);
    checkBalanceInWallet(currentAgent.getCreditWalletId(), newCreditBalanceForAgent);
    checkPlayerAgentBalance(player.getId(), newAgentPlayerBalance);

    List<WalletTransaction> allTransactions = walletTransactionRepository.findAll();
    assertThat(allTransactions, hasSize(1));
    WalletTransaction walletTransaction = allTransactions.get(0);
    assertThat(walletTransaction.getAmount(), equalTo(amount));
    assertThat(walletTransaction.getWalletId(), equalTo(currentAgent.getWalletId()));
    assertThat(walletTransaction.getOperationType(), equalTo(DEBIT));
    assertThat(walletTransaction.getNote(), equalTo(note));
    checkBalanceInWallet(currentAgent.getWalletId(), newBalanceForAgent, walletTransaction);

    List<UserTransaction> userTransactions = userTransactionRepository.findAll();
    assertThat(userTransactions, hasSize(1));
    UserTransaction userTransaction = userTransactions.get(0);
    assertThat(userTransaction.getUserid(), equalTo(currentAgent.getUserId()));
    assertThat(userTransaction.getPlayerid(), equalTo(player.getId()));
    assertThat(userTransaction.getOperationType(), equalTo(PAYOUT));
    assertThat(userTransaction.getAmount(), equalTo(amount));
    assertThat(userTransaction.getBalance(), equalTo(startBalanceForPlayerAgent + amount));
  }
}
