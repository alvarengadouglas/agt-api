package com.betmotion.agentsmanagement.rest.dto.agent;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class AgentDeactivationDto {

  Integer id;
  String userName;
  Integer agentId;
}
