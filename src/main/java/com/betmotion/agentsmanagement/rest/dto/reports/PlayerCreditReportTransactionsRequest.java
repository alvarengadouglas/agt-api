package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;
import java.util.List;

import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerCreditReportTransactionsRequest {

  LocalDateTime dateFrom;

  LocalDateTime dateTo;

  List<Long> playerIds;


  List<PlayerCreditTransactionType> transactionTypes;

  UserTransactionStatus transactionStatus;


}
