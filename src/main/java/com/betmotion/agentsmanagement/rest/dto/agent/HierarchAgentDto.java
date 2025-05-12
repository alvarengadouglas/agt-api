package com.betmotion.agentsmanagement.rest.dto.agent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HierarchAgentDto {

  Integer id;

  @JsonIgnore
  Integer parentId;

  String userName;

  List<HierarchAgentDto> child;

}
