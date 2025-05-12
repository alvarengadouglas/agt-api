package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WithdrawalUserResponseDto {

  @JsonProperty("username")
  private String userName;

  @JsonProperty("transactionId")
  private Long transactionId;

  @JsonProperty("wallet")
  private PlatformWalletDto wallet;
}
