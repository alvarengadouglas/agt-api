package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.AgentPlayer;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.UserTransaction;
import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import com.betmotion.agentsmanagement.platform.api.dto.*;
import com.betmotion.agentsmanagement.platform.api.service.PlatformInternalApi;
import com.betmotion.agentsmanagement.platform.queue.PlatformQueueAdapter;
import com.betmotion.agentsmanagement.rest.dto.player.CreatePlayerDto;
import com.betmotion.agentsmanagement.rest.dto.reports.*;
import com.betmotion.agentsmanagement.utils.DateFormatUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class PlatformApiService {

  PlatformApi platformApi;

  PlatformInternalApi platformInternalApi;

  AgentPlayerRepository agentPlayerRepository;

  AgentRepository agentRepository;

  PlayerRepository playerRepository;

  PlatformQueueAdapter platformQueueAdapter;

  HttpServletRequest request;

  public void changePlayerStatusWithPlatformApi(Player player, UserStatus userStatus) {
    ChangeStatusRequestDto changeStatusRequestDto = new ChangeStatusRequestDto();
    changeStatusRequestDto.setUserName(player.getUserName());
    changeStatusRequestDto.setStatus(userStatus);
    platformApi.changeStatus(changeStatusRequestDto);
  }

  public void changePassword(Player player, String newPassword) {
    Agent creator = getCreatorForPlayer(player);
    ChangePasswordRequestDto changeStatusRequestDto = new ChangePasswordRequestDto();
    changeStatusRequestDto.setUserName(player.getUserName());
    changeStatusRequestDto.setAgent(creator.getUser().getUserName());
    changeStatusRequestDto.setPassword(newPassword);
    platformApi.changePassword(changeStatusRequestDto);
  }

  public PlayerMoneyTransactionsReportDTO getMoneyTransactionsFromPlayer(Pageable pageable, Integer playerId) {
    Optional<Integer> platformId = playerRepository.findPlatformIdById(playerId);
    if(platformId.isEmpty()){
      return new PlayerMoneyTransactionsReportDTO(PlayerMoneyTransactionsReportResponse.Empty(),pageable.getPageSize());
    }
    UserTransactionFilterDTO userTransactionFilterDTO = new UserTransactionFilterDTO();
    userTransactionFilterDTO.setOrder("MOST_RECENT");
    userTransactionFilterDTO.setUserId(platformId.get());
    userTransactionFilterDTO.setPage((long) pageable.getPageNumber());
    userTransactionFilterDTO.setPageSize((long)pageable.getPageSize());
    userTransactionFilterDTO.setStartDate(DateFormatUtils.convertLocalDateTimeToDate(LocalDateTime.now().minusDays(1)));
    userTransactionFilterDTO.setEndDate(DateFormatUtils.convertLocalDateTimeToDate(LocalDateTime.now()));
    PlayerMoneyTransactionsReportResponse response = platformApi.getMoneyTransactionsFromPlayer(userTransactionFilterDTO);
    return new PlayerMoneyTransactionsReportDTO(response,pageable.getPageSize());
  }

  public void deposit(Player player, Long amount, Long remoteId, Long chargedRemoteId) {
    Agent creator = getCreatorForPlayer(player);
    DepositUserRequestDto requestDto = new DepositUserRequestDto();
    requestDto.setUserName(player.getUserName());
    requestDto.setAgent(creator.getUser().getUserName());
    requestDto.setAmount(amount);
    requestDto.setRemoteId(remoteId);
    requestDto.setChargedRemoteId(chargedRemoteId);
    platformQueueAdapter.sendDeposit(requestDto);
  }

  public void withdraw(Long amount, Player player, UserTransaction withdrawalTransaction, Long chargedRemoteId) {
    Agent creator = getCreatorForPlayer(player);
    WithdrawalUserRequestDto requestDto = new WithdrawalUserRequestDto();
    requestDto.setUserName(player.getUserName());
    requestDto.setAgent(creator.getUser().getUserName());
    requestDto.setAmount(amount);
    requestDto.setRemoteId(withdrawalTransaction.getId().longValue());
    requestDto.setChargedRemoteId(chargedRemoteId);
    platformQueueAdapter.sendWithdraw(requestDto);
  }

  private Agent getCreatorForPlayer(Player player) {
    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(player.getId());
    return agentRepository.getEntity(agentPlayer.getAgentId());
  }

  public Integer registerUserInPlatform(CreatePlayerDto dto, Agent agent) {
    String agentName = agent.getUser().getUserName();
    RegisterUserRequestDto requestDto = new RegisterUserRequestDto();
    requestDto.setUserName(dto.getUserName());
    requestDto.setPassword(dto.getPassword());
    requestDto.setEmail(dto.getEmail());
    requestDto.setAgent(agentName);
    requestDto.setFirstName(dto.getFirstName());
    requestDto.setLastName(dto.getLastName());
    requestDto.setCellPhone(dto.getPhoneNumber());
    requestDto.setCellPhoneCountryId(dto.getCountryId());
    requestDto.setPromocode(dto.getPromocode());
    requestDto.setIpAddress(request.getRemoteAddr());
    RegisterUserResponseDto result = platformApi.createUser(requestDto);
    log.info("User is created with id: {}", result.getId());
    return result.getId();
//    platformQueueAdapter.createUser(requestDto);
  }

  public List<PlatformUserDto> getPlatformUserDtos(List<Player> players) {
    Set<Integer> platformIds = players
        .stream()
        .map(Player::getPlatformId)
        .collect(toSet());
    FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
    requestDto.setUserIds(platformIds);
    return platformApi.findByIds(requestDto);
  }

  public CommissionAgent getCommissionReport(CommissionPayload payload) {
    return platformInternalApi.getCommissionReport(payload);
  }

  public CommissionAgent getCommissionReportNew(CommissionPayload payload) {
    return platformInternalApi.getCommissionReportNew(payload);
  }

  public CommissionAgent getCommissionReportJava(CommissionPayload payload) {
    return platformInternalApi.getCommissionReportJava(payload);
  }
}
