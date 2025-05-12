package com.betmotion.agentsmanagement.rest.dto.reports;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AgentPaymentReportRequest {

  private Integer agentId;
  private LocalDateTime beginDate;
  private LocalDateTime endDate;
  private String searchAgentName;
  private int limitPerPage;
  private int pageNumber;
  

}
