package com.betmotion.agentsmanagement.rest.dto.agent;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgentCreditOperation {

  @NotNull
  private Integer agentId;

  @NotNull
  @Min(value = 1L)
  private Long amount;

  @NotNull
  private Long bonus;
}
