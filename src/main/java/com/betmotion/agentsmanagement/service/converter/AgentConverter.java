package com.betmotion.agentsmanagement.service.converter;

import static com.betmotion.agentsmanagement.utils.Constants.YYYYMMDDD_FORMAT;
import static com.betmotion.agentsmanagement.utils.DateFormatUtils.formatDate;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.HierarchAgentDto;
import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class AgentConverter {

  public static HierarchAgentDto buildHierarchy(Agent item, Integer defaultParentId) {
    HierarchAgentDto result = new HierarchAgentDto();
    result.setParentId(defaultIfNull(item.getParentId(), defaultParentId));
    result.setId(item.getId());
    result.setUserName(item.getUser().getUserName());
    return result;
  }

  public static HierarchAgentDto buildHierarchy(Agent creator, Player player) {
    HierarchAgentDto result = new HierarchAgentDto();
    result.setParentId(creator.getId());
    result.setId(player.getId());
    result.setUserName(player.getUserName());
    return result;
  }

  public static AgentDto convertUser(Agent agent) {
    AgentDto result = new AgentDto();
    result.setId(agent.getId());
    result.setUserName(agent.getUser().getUserName());
    result.setFullName(agent.getUser().getFirstName());
    result.setEmail(agent.getUser().getEmail());
    result.setPhoneNumber(agent.getUser().getPhone());
    result.setCommission(agent.getCommission());
    result.setCommissionType(agent.getCommissionType());
    result.setCommissionCasino(agent.getCommissionCasino());
    result.setCommissionSlots(agent.getCommissionSlots());
    result.setCommissionSports(agent.getCommissionSports());
    result.setCanHaveSubAgents(agent.isCanHaveSubAgents());
    result.setCode(agent.getCode().getCode());
    result.setLastUpdated(agent.getLastCommissionUpdate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    result.setCreatedDate(formatDate(agent.getUser().getCreatedOn(), YYYYMMDDD_FORMAT));
    return result;
  }
}
