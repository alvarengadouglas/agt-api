package com.betmotion.agentsmanagement.powerbi.api;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class AccessTokenDto {

  @JsonProperty("token_type")
  String tokenType;

  @JsonProperty("access_token")
  String accessToken;
}
