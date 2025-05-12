package com.betmotion.agentsmanagement.rest.dto.agent;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LinkUsersDto {

  @NotEmpty
  List<Integer> userIds;
}
