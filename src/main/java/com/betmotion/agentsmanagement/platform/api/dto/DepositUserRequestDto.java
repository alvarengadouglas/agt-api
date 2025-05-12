package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DepositUserRequestDto implements Serializable {

  @JsonProperty("username")
  private String userName;

  @JsonProperty("agent")
  private String agent;

  @JsonProperty("amount")
  private Long amount;

  @JsonProperty("remoteId")
  private Long remoteId;

  @JsonProperty("chargedRemoteId")
  private Long chargedRemoteId;
}
