package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

import java.util.List;

@Data
public class CommissionRequest {
    private String beginDate;
    private String endDate;
    private String usernames;
}
