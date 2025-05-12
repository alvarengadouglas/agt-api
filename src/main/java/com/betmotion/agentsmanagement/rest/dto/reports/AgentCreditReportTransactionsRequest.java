package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentCreditReportTransactionsRequest {

  LocalDateTime dateFrom;

  LocalDateTime dateTo;

  List<Integer> agentIds;


  List<AgentRestTransactionType> transactionTypes;


}
