package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {

  @JsonProperty("username")
  private String userName;

  @JsonProperty("password")
  private String password;

  @JsonProperty("agent")
  private String agent;
}
