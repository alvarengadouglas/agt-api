package com.betmotion.agentsmanagement.rest.dto.agent;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAgentDto extends AbstractAgentDto {

  @NotEmpty
  String userName;

  @NotEmpty
  String password;

}