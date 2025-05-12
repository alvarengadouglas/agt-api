package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AgentProviderReportRequest {
	
	private LocalDateTime beginDate;
	private LocalDateTime endDate;
	private String providerName;
	private Integer playerId;
	private Integer agentId;

}
