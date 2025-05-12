package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class CommissionReport {
    public CommissionAgent commissionAgents;
    private BigDecimal commissionCasino;

    private BigDecimal commissionSlots;

    private BigDecimal commissionSports;

}
