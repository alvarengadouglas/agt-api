package com.betmotion.agentsmanagement.rest.dto.reports;

import java.math.BigInteger;

import lombok.Data;

@Data
public class AgentEspecificProviderReportRow {

	private Integer gameId;
	private String gameName;
	private String originProvider;
	private Integer totalCountBet;
	private BigInteger totalAmountBet;
	private BigInteger totalAmountWin;
	private BigInteger totalAmountNetWin;
	
}
