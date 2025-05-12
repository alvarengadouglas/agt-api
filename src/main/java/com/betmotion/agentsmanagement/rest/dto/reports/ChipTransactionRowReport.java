package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

@Data
public class ChipTransactionRowReport {

  @JsonIgnore
  private Integer agentId;
  @JsonIgnore
  private Integer parentAgentId;

  private String sourceName;

  private String targetName;

  private BigInteger amount;

  private BigInteger bonus;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
  private LocalDateTime operationDate;

  private AgentTransactionType operationType;

  private UserTransactionStatus status;

  public void setOperationDate(Timestamp operationDate) {
    this.operationDate = operationDate.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
  }

  public void setOperationType(String operationType) {
    this.operationType = AgentTransactionType.valueOf(operationType);
  }

  public void setStatus(String userTransactionStatus) {
    this.status = userTransactionStatus != null ? UserTransactionStatus.valueOf(userTransactionStatus) : null;
  }
}
