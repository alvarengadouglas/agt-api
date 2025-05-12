package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class AgentTransactionsRequest {

  private List<Integer> agentIds;

  private List<AgentRestTransactionType> transactionTypes;

  private LocalDateTime dateFrom;

  private LocalDateTime dateTo;

}
