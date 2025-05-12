package com.betmotion.agentsmanagement.service;

import static java.util.Optional.empty;
import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.RedisKey;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.rest.dto.user.UserDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserRole;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserService {

  UserProvider userProvider;

  AgentRepository agentRepository;
  AgentPlayerRepository agentPlayerRepository;

  @Autowired
  private GenericRedisService<UserDto> redisService;

  @Transactional(readOnly = true)
  public Optional<UserDto> getCurrentUser() {

    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();

    String redisKey = RedisKey.CURRENT_USER_DETAILS.getRedisKey(currentUserDetails.getUsername());

    Optional<UserDto> cachedUser = redisService.<UserDto>findByKeyAndConvert(redisKey, UserDto.class);
    if(cachedUser.isPresent()){
      return cachedUser;
    }

    if (currentUserDetails instanceof AppUserDetails) {
      AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
      UserDto result = new UserDto();
      convertData(appUserDetails, result);

      redisService.convertAndSave(redisKey, result, 24, TimeUnit.HOURS);

      return Optional.of(result);
    }

    return empty();
  }

  public void clearUserCache(User user){
    String redisKey = RedisKey.CURRENT_USER_DETAILS.getRedisKey(user.getUserName());
    redisService.delete(redisKey);
  }

  private void convertData(AppUserDetails appUserDetails, UserDto result) {
    result.setUserName(appUserDetails.getUsername());
    result.setRole(appUserDetails.getRole());
    result.setAuthorities(appUserDetails.getAuthorityStrings());
    assignData(appUserDetails, result);
  }

  private void assignData(AppUserDetails appUserDetails, UserDto result) {
    Agent agent = agentRepository.getEntity(appUserDetails.getId());

    result.setAgentCode(agent.getCode().getCode());
    result.setBalance(agent.getBalance());
    result.setCredits(agent.getCreditsWallet().getBalance());
    result.setEmail(agent.getUser().getEmail());
    result.setCommission(agent.getCommission());
    result.setCommissionCasino(agent.getCommissionCasino());
    result.setCommissionSlots(agent.getCommissionSlots());
    result.setCommissionSports(agent.getCommissionSports());
    result.setParentId(agent.getParentAgent().getId());
    result.setParentUserName(agent.getParentAgent().getUser().getUserName());
    result.setAgentsCount(agentRepository.countAgentsByParentId(agent.getId()));
    result.setPlayersCount(agentPlayerRepository.countPlayersByAgentId(agent.getId()));
    
    String firstName = agent.getUser().getFirstName() != null ? agent.getUser().getFirstName() : "";
    String lastName = agent.getUser().getLastName() != null ? agent.getUser().getLastName() : "";
    result.setFullName(firstName + " " + lastName);
  }

}
