package com.betmotion.agentsmanagement.rest.dto.agent;

import java.math.BigDecimal;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractAgentDto {

  @Email
  String email;
  String phoneNumber;
  String fullName;
  Integer parentAgentId;

  @NotNull
  BigDecimal commission;
  BigDecimal commissionCasino;
  BigDecimal commissionSlots;
  BigDecimal commissionSports;

  @NotNull
  String commissionType;


  boolean canHaveSubAgents;
}
