package com.betmotion.agentsmanagement.rest.dto.agent;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepositAgentUserBalanceOperation {

  @NotNull
  private Integer playerId;

  @NotNull
  @Min(value = 1)
  private Long amount;

  private Long bonus;

  @NotNull
  private Boolean forFree;
}
