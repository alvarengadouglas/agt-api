package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.config.CodeConfiguration;
import com.betmotion.agentsmanagement.domain.AgentCode;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class AgentCodeGeneratorService {

  AgentCodeService agentCodeService;

  CodeConfiguration codeConfiguration;

  public AgentCode generateNewCode() {
    int currentAttempt = 0;
    while (currentAttempt < codeConfiguration.getMaxAttempts()) {
      try {
        return agentCodeService.generateCode();
      } catch (Exception e) {
        log.error("Error during generation", e);
        currentAttempt++;
      }
    }
    throw new ServiceException("A003", new String[]{});
  }
}
