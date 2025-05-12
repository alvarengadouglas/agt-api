package com.betmotion.agentsmanagement.rest.dto.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.Data;

@Data
public class AgentPaymentChipReport {

  private List<AgentPaymentChipRowReport> agentPaymentChipRowReport;

  private long totalAgents;

  private BigInteger totalChipIn;

  private BigInteger totalBonus;

  private BigInteger totalChipOut;

  private BigInteger totalBalanceChip;

  private BigDecimal commissionPercent;

  private BigDecimal comission;

  private BigDecimal totalPay;

}
