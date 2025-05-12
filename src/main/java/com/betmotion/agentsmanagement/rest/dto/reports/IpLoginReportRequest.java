package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IpLoginReportRequest {

    private LocalDateTime beginDate;
    private LocalDateTime endDate;
    private String role;
    private Boolean onlyRepeated;
}
