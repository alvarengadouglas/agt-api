package com.betmotion.agentsmanagement.service.converter;

import static com.betmotion.agentsmanagement.utils.Constants.YYYYMMDDD_FORMAT;
import static com.betmotion.agentsmanagement.utils.DateFormatUtils.formatDate;

import com.betmotion.agentsmanagement.dao.projection.UserDetailInfo;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.rest.dto.user.UserDetailInfoDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDetailInfoConverter {

  public static UserDetailInfoDto convert(UserDetailInfo value) {
    UserDetailInfoDto result = new UserDetailInfoDto();
    result.setId(value.getId());
    result.setUserName(value.getUserName());
    result.setFullName(value.getFullName());
    result.setEmail(value.getEmail());
    result.setCommissionType(value.getCommissionType());
    result.setCommission(value.getCommission());
    result.setCommissionSports(value.getCommissionSports());
    result.setCommissionSlots(value.getCommissionSlots());
    result.setCommissionCasino(value.getCommissionCasino());
    result.setPhone(value.getPhone());
    result.setRole(UserRole.valueOf(value.getRole()));
    result.setCreatedOn(formatDate(value.getCreatedOn(), YYYYMMDDD_FORMAT));
    result.setParentUserName(value.getParentUserName());
    result.setNumberOfDirectSubAgents(value.getNumberOfDirectSubAgents());
    result.setNumberOfDirectPlayers(value.getNumberOfDirectPlayers());
    return result;
  }
}
