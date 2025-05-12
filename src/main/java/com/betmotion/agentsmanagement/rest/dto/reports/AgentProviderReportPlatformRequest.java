package com.betmotion.agentsmanagement.rest.dto.reports;

import java.util.List;

import lombok.Data;

@Data
public class AgentProviderReportPlatformRequest {

	private String beginDateString;
	private String endDateString;
	private String agentOwner;
	private String providerName;
	private Integer userId;
	private List<String> agentsName;
	
}
