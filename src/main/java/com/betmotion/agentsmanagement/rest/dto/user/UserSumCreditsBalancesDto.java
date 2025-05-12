package com.betmotion.agentsmanagement.rest.dto.user;

import com.betmotion.agentsmanagement.dao.projection.UserSumCreditsAndBalance;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSumCreditsBalancesDto {
  Long balance;
  Long credits;

  public UserSumCreditsBalancesDto(Long balance, Long credits) {
    this.balance = balance;
    this.credits = credits;
  }

  public static UserSumCreditsBalancesDto fromUserSumCreditsAndBalance(
      UserSumCreditsAndBalance userSumCreditsAndBalance) {
    return new UserSumCreditsBalancesDto(userSumCreditsAndBalance.getBalance(), userSumCreditsAndBalance.getCredits());
  }
}
