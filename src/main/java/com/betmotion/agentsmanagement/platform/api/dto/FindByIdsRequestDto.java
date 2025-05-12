package com.betmotion.agentsmanagement.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.Data;

@Data
public class FindByIdsRequestDto {

  @JsonProperty("userIds")
  private Set<Integer> userIds;
}
