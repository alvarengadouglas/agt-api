package com.betmotion.agentsmanagement.rest.dto.agent;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalAgentUserBalanceOperation {


  @NotNull
  private Integer playerId;

  @NotNull
  @Min(value = 1)
  private Long amount;

  @NotNull
  private Boolean forFree;

}