package com.betmotion.agentsmanagement.platform.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserTransactionDTO implements Serializable {

	private static final long serialVersionUID = 5293762570645855906L;
	
	private Long id;
	private String roundId;
    private String date;
    private String type;
    private String transactionMode;
    private Long credit;
    private Long debit;
    private Long balance;
    private Long bonusBalance;
    private Long bonusAmount;
    private Long moneyAmount;
    private String game;
    private Long indebtedness;

}
