package com.betmotion.agentsmanagement.dao.projection;

import lombok.Value;

@Value
public class UserSumCreditsAndBalance {
  Long balance;
  Long credits;
}
