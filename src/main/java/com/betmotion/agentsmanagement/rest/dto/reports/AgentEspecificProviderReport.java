package com.betmotion.agentsmanagement.rest.dto.reports;

import java.math.BigInteger;
import java.util.List;

import lombok.Data;

@Data
public class AgentEspecificProviderReport {
	
	private Integer totalCountBet;
	private BigInteger totalAmountBet;
	private BigInteger totalAmountWin;
	private List<AgentEspecificProviderReportRow> providerReport;

}
