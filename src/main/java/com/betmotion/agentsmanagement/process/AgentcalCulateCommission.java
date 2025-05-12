package com.betmotion.agentsmanagement.process;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import com.betmotion.agentsmanagement.rest.dto.reports.AgentPaymentChipRowReport;

import lombok.Getter;

public class AgentcalCulateCommission {

  @Getter
  private BigDecimal commissionPercent;

  @Getter
  private BigDecimal commission;

  @Getter
  private BigDecimal totalPay;

  public AgentcalCulateCommission(BigDecimal commissionPercent, List<AgentPaymentChipRowReport> agentPaymentChipRow) {

    this.commissionPercent = commissionPercent;

    if (agentPaymentChipRow.isEmpty()) {
      return;
    }

    this.commission = calculateCommission(agentPaymentChipRow);
    this.totalPay = calculateTotalPay(agentPaymentChipRow);
  }

  private BigDecimal calculateCommission(List<AgentPaymentChipRowReport> agentPaymentChipRow) {
    BigDecimal result = new BigDecimal(sumBalanceChip(agentPaymentChipRow));
    return result.multiply(this.commissionPercent.divide(new BigDecimal("100").setScale(2, RoundingMode.HALF_UP)));
  }

  private BigInteger sumBalanceChip(List<AgentPaymentChipRowReport> agentPaymentChipRow) {
    return agentPaymentChipRow.stream().map(AgentPaymentChipRowReport::getTotalChip).reduce(BigInteger.ZERO, BigInteger::add);
  }

  private BigDecimal calculateTotalPay(List<AgentPaymentChipRowReport> agentPaymentChipRow) {
    BigDecimal amountChip = new BigDecimal(sumBalanceChip(agentPaymentChipRow));
    return amountChip.subtract(this.commission).setScale(2, RoundingMode.HALF_UP);
  }

}
