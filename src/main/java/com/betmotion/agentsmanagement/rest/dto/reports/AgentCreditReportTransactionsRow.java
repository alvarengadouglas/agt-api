package com.betmotion.agentsmanagement.rest.dto.reports;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentCreditReportTransactionsRow {

  AgentRestTransactionType transactionType;

  Long amount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
  LocalDateTime transactionDate;

  ReportUserInfo directAgent;

  ReportUserInfo agent;

  boolean superior;

  ReportUserInfo executor;

  Long bonus;

}
