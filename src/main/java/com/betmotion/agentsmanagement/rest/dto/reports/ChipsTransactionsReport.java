package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class ChipsTransactionsReport {

  private List<ChipTransactionRowReport> agentPaymentChipRowReport;

  private BigInteger totalBalance;

}
