package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterUserRequestDto {

  @JsonProperty("username")
  private String userName;

  @JsonProperty("password")
  private String password;

  @JsonProperty("email")
  private String email;

  @JsonProperty("agent")
  private String agent;

  @JsonProperty("firstName")
  private String firstName;

  @JsonProperty("lastName")
  private String lastName;

  @JsonProperty("cellPhone")
  private String cellPhone;

  @JsonProperty("cellPhoneCountryId")
  private Integer cellPhoneCountryId;

  @JsonProperty("promocode")
  private String promocode;

  @JsonProperty("ipAddress")
  private String ipAddress;
}
