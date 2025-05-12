package com.betmotion.agentsmanagement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.AgentTransactionRepository;
import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.dao.PlayerWalletRepository;
import com.betmotion.agentsmanagement.dao.UserRepository;
import com.betmotion.agentsmanagement.dao.UserTransactionRepository;
import com.betmotion.agentsmanagement.dao.WalletRepository;
import com.betmotion.agentsmanagement.dao.WalletTransactionRepository;
import com.betmotion.agentsmanagement.domain.PlayerWallet;
import com.betmotion.agentsmanagement.domain.Wallet;
import com.betmotion.agentsmanagement.domain.WalletTransaction;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.betmotion.agentsmanagement.powerbi.config.PowerBiTokenConfiguration;
import com.betmotion.agentsmanagement.service.AgentService;
import com.betmotion.agentsmanagement.service.AgentTransactionService;
import com.betmotion.agentsmanagement.service.CreditsService;
import com.betmotion.agentsmanagement.service.ServicesConfiguration;
import com.betmotion.agentsmanagement.service.UserProvider;
import com.betmotion.agentsmanagement.service.UserTransactionService;
import com.betmotion.agentsmanagement.service.WalletTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PROTECTED)
@AutoConfigureWireMock(httpsPort = 0, port = 0)
@TestMethodOrder(MethodOrderer.MethodName.class)
public abstract class AbstractIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  ServicesConfiguration servicesConfiguration;

  @Autowired
  DataSource dataSource;

  @Autowired
  AgentRepository agentRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  UserProvider userProvider;

  @Autowired
  AgentService agentService;

  @Autowired
  PlayerRepository playerRepository;

  @Autowired
  AgentPlayerRepository agentPlayerRepository;

  @Autowired
  WalletRepository walletRepository;

  @Autowired
  UserTransactionRepository userTransactionRepository;

  @Autowired
  WalletTransactionRepository walletTransactionRepository;

  @Autowired
  PlayerWalletRepository playerWalletRepository;

  @Autowired
  CreditsService creditsService;

  @Autowired
  UserTransactionService userTransactionService;

  @Autowired
  WalletTransactionService walletTransactionService;

  @Autowired
  AgentTransactionService agentTransactionService;

  @Autowired
  PowerBiTokenConfiguration powerBiTokenConfiguration;

  @Autowired
  AgentTransactionRepository agentTransactionRepository;


  protected void updateBalanceForWallet(Integer walletId, Long balance) {
    Wallet agentWallet = walletRepository.getEntity(walletId);
    agentWallet.setBalance(balance);
    walletRepository.save(agentWallet);
  }

  protected void checkBalanceInWallet(Integer walletId, Long balance) {
    Wallet agentWallet = walletRepository.getEntity(walletId);
    assertThat(agentWallet.getBalance(), equalTo(balance));
  }

  protected void checkBalanceInWallet(Integer walletId, Long balance,
      WalletTransaction transaction) {
    Wallet agentWallet = walletRepository.getEntity(walletId);
    assertThat(agentWallet.getBalance(), equalTo(balance));
    assertThat(transaction.getBalance(), equalTo(agentWallet.getBalance()));
  }

  protected void checkPlayerAgentBalance(Integer playerId, Long balance) {
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);
    assertThat(playerWallet.getBalance(), equalTo(balance));
  }

  protected void updatePlayerBalanceForAgent(Integer playerId, Long balance) {
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);
    playerWallet.setBalance(balance);
    playerWalletRepository.save(playerWallet);
  }

  protected PlatformUserDto buildPlatformUserDto(String userName, Long balance, String status,
      Integer id) {
    PlatformUserDto result = new PlatformUserDto();
    result.setAmount(balance);
    result.setUserName(userName);
    result.setId(id);
    result.setStatus(status);
    return result;
  }

  protected PlatformUserDto buildPlatformUserDto(String userName, Long balance, String status,
      Integer id, String email) {
    PlatformUserDto result = buildPlatformUserDto(userName, balance, status, id);
    result.setEmail(email);
    return result;
  }
}
