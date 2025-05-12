package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentCommissions {

    private BigDecimal commissionCasino;

    private BigDecimal commissionSlots;

    private BigDecimal commissionSports;

}
