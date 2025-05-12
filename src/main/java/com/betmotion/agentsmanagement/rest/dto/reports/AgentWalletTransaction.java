package com.betmotion.agentsmanagement.rest.dto.reports;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AgentWalletTransaction {

  private String agentName;

  private Long amount;

  private Long balance;

  private Long bonus;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
  private LocalDateTime transactionDate;

  private AgentRestTransactionType transactionType;

  private String note;
}