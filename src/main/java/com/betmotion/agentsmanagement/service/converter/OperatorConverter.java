package com.betmotion.agentsmanagement.service.converter;

import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperatorConverter {

  public static OperatorDto convert(Agent operator) {
    OperatorDto result = new OperatorDto();
    result.setId(operator.getId());
    result.setUserName(operator.getUser().getUserName());
    return result;
  }
}
