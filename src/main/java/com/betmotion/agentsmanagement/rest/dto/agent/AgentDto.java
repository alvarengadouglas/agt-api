package com.betmotion.agentsmanagement.rest.dto.agent;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AgentDto implements HierarchyAware {

  Integer id;
  String userName;
  String fullName;
  String email;
  String phoneNumber;
  BigDecimal commission;
  String commissionType;
  String lastUpdated;
  BigDecimal commissionCasino;
  BigDecimal commissionSlots;
  BigDecimal commissionSports;
  boolean canHaveSubAgents;
  String code;
  String createdDate;
  List<HierarchAgentDto> hierarchy;
}
