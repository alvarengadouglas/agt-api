package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerCreditReportTransactionsResponse {

  PageData<PlayerCreditReportTransactionsRow> data;

  Long bonusTotal;

  Long amountTotal;

  Long total;
}
