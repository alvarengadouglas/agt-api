package com.betmotion.agentsmanagement.rest.dto.reports;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Data;

@Data
public class AgentPaymentChipRowReport {

  private Integer agentId;

  private String agentName;

  private BigDecimal commission;

  private BigInteger chipIn;

  private BigInteger bonus;

  private BigInteger chipOut;

  private BigInteger totalChip;

  private Integer parentAgentId;

  private String parentTree;

  public void addValues(AgentPaymentChipRowReport child) {
    this.chipIn = this.chipIn.add(child.chipIn);
    this.bonus = this.bonus.add(child.bonus);
    this.chipOut = this.chipOut.add(child.chipOut);
    this.totalChip = this.totalChip.add(child.totalChip);
  }

}
