package com.betmotion.agentsmanagement.service.converter;

import com.betmotion.agentsmanagement.dao.projection.UserInfo;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserInfoConverter {

  public static UserInfoDto convert(UserInfo value, Long currentAgentId) {
    UserInfoDto result = new UserInfoDto();
    result.setUserName(value.getUserName());
    result.setRole(UserRole.valueOf(value.getRole()));
    result.setCredits(value.getCredits());
    result.setBalance(value.getBalance());
    result.setId(value.getId());
    result.setPlatformId(value.getPlatformId());
    result.setStatus(value.getStatus());
    result.setDirect(currentAgentId.equals(value.getParentId()));
    return result;
  }
}
