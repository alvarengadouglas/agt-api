package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangeStatusResponseDto {

  @JsonProperty("username")
  private String userName;

  @JsonProperty("status")
  private UserStatus status;
}
