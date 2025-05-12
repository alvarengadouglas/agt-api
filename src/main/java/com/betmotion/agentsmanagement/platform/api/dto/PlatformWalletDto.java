package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlatformWalletDto {

  @JsonProperty("balance")
  Long balance;

  @JsonProperty("currency")
  String currency;
}