package com.betmotion.agentsmanagement.rest.dto.agent;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AgentTotalQueryDTO {

    BigInteger total;
    BigInteger amount;
    BigInteger bonus;

}
