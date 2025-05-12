package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.rest.dto.PageData;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentCreditReportTransactionsResponse {

  PageData<AgentCreditReportTransactionsRow> data;

  Long bonusTotal;

  Long amountTotal;

  Long total;
}
