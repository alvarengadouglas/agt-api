package com.betmotion.agentsmanagement.rest.dto.agent;

import java.util.List;

public interface HierarchyAware {

  void setHierarchy(List<HierarchAgentDto> hierarchy);

}
