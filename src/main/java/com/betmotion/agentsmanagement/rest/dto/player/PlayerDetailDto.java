package com.betmotion.agentsmanagement.rest.dto.player;

import static com.betmotion.agentsmanagement.utils.Constants.ISO_DATE_TIME_FORMAT_VALUE;

import com.betmotion.agentsmanagement.rest.dto.agent.HierarchAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.HierarchyAware;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerDetailDto implements HierarchyAware {

  Integer id;

  String userName;

  String fullName;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_DATE_TIME_FORMAT_VALUE)
  Date createdOn;
  String email;

  String phone;

  String parent;

  List<HierarchAgentDto> hierarchy;

}
