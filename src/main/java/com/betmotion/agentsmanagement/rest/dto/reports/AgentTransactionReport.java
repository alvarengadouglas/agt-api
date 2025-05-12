package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentTransactionReport {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
    private LocalDateTime operationDate;
    private String userName;
    private AgentTransactionType agentTransactionType;
    private BigInteger amount;
    private BigInteger bonus;

    public void setOperationDate(Timestamp operationDate) {
        this.operationDate = operationDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
    }

    public void setAgentTransactionType(String operationType) {
        this.agentTransactionType = AgentTransactionType.valueOf(operationType);
    }
}
