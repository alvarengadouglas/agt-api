package com.betmotion.agentsmanagement.rest.dto.reports;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CommissionAgent {
    private BigInteger cassinoBets;
    private BigInteger cassinoBetsValue;
    private BigInteger cassinoWins;
    private BigInteger slotsBets;
    private BigInteger slotsBetsValue;
    private BigInteger slotsWins;
    private BigInteger sportsBets;
    private BigInteger sportsBetsValue;
    private BigInteger sportsWins;
}
