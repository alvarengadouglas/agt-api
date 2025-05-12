package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data

public class PlayerTransactionsRequest {

  private List<Long> playerIds;

  private List<PlayerCreditTransactionType> transactionTypes;

  private LocalDateTime dateFrom;

  private LocalDateTime dateTo;
}