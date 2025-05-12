package com.betmotion.agentsmanagement.service.converter;

import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.rest.dto.player.PlayerDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

  public static PlayerDto convertPlayer(User user) {
    PlayerDto result = new PlayerDto();
    result.setId(user.getId());
    result.setCreatedOn(user.getCreatedOn());
    result.setEmail(user.getEmail());
    result.setLastLogin(user.getLastLogin());
    result.setPhone(user.getPhone());
    result.setReceiveEmail(user.getReceiveEmail());
    result.setRole(user.getRole());
    result.setStatus(user.getStatus());
    result.setTestUser(user.getTestUser());
    result.setUserName(user.getUserName());
    result.setAgentId(user.getAgentId());
    return result;
  }
}
