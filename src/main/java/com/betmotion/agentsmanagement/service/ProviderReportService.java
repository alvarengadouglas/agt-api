package com.betmotion.agentsmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import com.betmotion.agentsmanagement.rest.dto.agent.ExtendedInfoAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.HierarchAgentDto;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentEspecificProviderReport;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentProviderReport;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentProviderReportPlatformRequest;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentProviderReportRequest;
import com.betmotion.agentsmanagement.utils.DateFormatUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProviderReportService {

	PlatformApi platformApi;
	AgentService agentService;

	@Transactional
	public AgentProviderReport getProviderReport(AgentProviderReportRequest providerReportRequest) {
		ExtendedInfoAgentDto agentsHierarchy = agentService.getAgentHierarchy(providerReportRequest.getAgentId());
		AgentProviderReportPlatformRequest filterRequest = createFilterRequestToPlatform(providerReportRequest, agentsHierarchy);
		return platformApi.providerReport(filterRequest);

	}

	@Transactional
	public AgentEspecificProviderReport getEspecificProviderReport(AgentProviderReportRequest providerReportRequest) {
		ExtendedInfoAgentDto agentsHierarchy = agentService.getAgentHierarchy(providerReportRequest.getAgentId());
		AgentProviderReportPlatformRequest filterRequest = createFilterRequestToPlatform(providerReportRequest, agentsHierarchy);
		return platformApi.especificProviderReport(filterRequest);
	}
	
	private AgentProviderReportPlatformRequest createFilterRequestToPlatform(AgentProviderReportRequest providerReportRequest, ExtendedInfoAgentDto agentsHierarchy) {
		AgentProviderReportPlatformRequest providerReportPlatform = new AgentProviderReportPlatformRequest();
		providerReportPlatform.setBeginDateString(DateFormatUtils.formatDateTime(providerReportRequest.getBeginDate()));
		providerReportPlatform.setEndDateString(DateFormatUtils.formatDateTime(providerReportRequest.getEndDate()));
		providerReportPlatform.setAgentOwner(agentsHierarchy.getUserName());
		providerReportPlatform.setAgentsName(getAgentsName(agentsHierarchy));
		providerReportPlatform.getAgentsName().add(agentsHierarchy.getUserName());
		providerReportPlatform.setUserId(providerReportRequest.getPlayerId());
		providerReportPlatform.setProviderName(providerReportRequest.getProviderName());
		return providerReportPlatform;
	}

	private List<String> getAgentsName(ExtendedInfoAgentDto agentsHierarchy) {
		List<String> agentsName = new ArrayList<>();
		if(CollectionUtils.isEmpty(agentsHierarchy.getChild())) {
			return agentsName;
		}
		return getAgentNameAllHierarchy(agentsName, agentsHierarchy.getChild());
	}
	
	private List<String> getAgentNameAllHierarchy(List<String> agentsName, List<HierarchAgentDto> child) {
		for (HierarchAgentDto hierarchAgentDto : child) {
			agentsName.add(hierarchAgentDto.getUserName());
			if (!CollectionUtils.isEmpty(hierarchAgentDto.getChild())) {
				getAgentNameAllHierarchy(agentsName, hierarchAgentDto.getChild());
			}
		}
		return agentsName;
		
	}

}
