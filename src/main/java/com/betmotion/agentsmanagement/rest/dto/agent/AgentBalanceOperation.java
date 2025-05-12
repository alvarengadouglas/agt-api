package com.betmotion.agentsmanagement.rest.dto.agent;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgentBalanceOperation {

  @NotNull
  private Integer agentId;

  @NotNull
  @Min(value = 1)
  private Long balance;

}