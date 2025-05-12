package com.betmotion.agentsmanagement.rest.dto.agent;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAgentDto extends AbstractAgentDto {
  String userName;
  Boolean commissionUpdated;
}