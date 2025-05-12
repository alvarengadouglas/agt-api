package com.betmotion.agentsmanagement.rest.dto.reports;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PlayerCreditsTransaction {

  private Integer playerId;

  private String playerName;

  private Long amount;

  private Long bonus;

  private Long balance;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
  private LocalDateTime transactionDate;

  private PlayerCreditTransactionType transactionType;

  private String note;

  private UserTransactionStatus status;
}