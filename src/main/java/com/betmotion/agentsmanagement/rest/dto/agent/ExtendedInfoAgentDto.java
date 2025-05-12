package com.betmotion.agentsmanagement.rest.dto.agent;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExtendedInfoAgentDto implements HierarchyAware {

  String userName;

  Integer id;

  List<HierarchAgentDto> child;

  @Override
  public void setHierarchy(List<HierarchAgentDto> hierarchy) {
    child = hierarchy;
  }
}
