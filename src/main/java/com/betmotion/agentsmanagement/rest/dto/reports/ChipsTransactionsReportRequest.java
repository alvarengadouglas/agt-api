package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ChipsTransactionsReportRequest {

  private UserRole userRole;
  private LocalDateTime beginDate;
  private LocalDateTime endDate;
  private Integer agentId;
  private String playerName;
  private List<AgentTransactionType> transactionType;
  private Boolean myTransactions;
  private Integer loggedAgentId;
  private UserTransactionStatus transactionStatus;

  public List<String> getTransactionTypesString() {
    return transactionType.stream().map(Enum::name).collect(Collectors.toList());
  }
}