package com.betmotion.agentsmanagement.rest.dto;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayoutAgentDto {

  @NotNull
  @Min(value = 1)
  private Long amount;

  @NotNull
  private Integer agentId;

  private String note;

}
