package com.betmotion.agentsmanagement.rest.dto.reports;

import java.math.BigInteger;

import lombok.Data;

@Data
public class AgentProviderReportRow {

	private String originProvider;
	private Integer totalCountBet;
	private BigInteger totalAmountBet;
	private BigInteger totalAmountWin;
	private BigInteger totalAmountNetWin;
	
}
