package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

@Data
public class CommissionPayload {
    private Integer agentId;
    private String beginDate;
    private String endDate;
}
