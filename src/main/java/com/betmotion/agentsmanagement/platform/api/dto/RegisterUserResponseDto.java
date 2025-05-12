package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUserResponseDto {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("username")
  private String userName;
}
