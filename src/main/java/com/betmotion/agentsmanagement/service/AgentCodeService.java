package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.betmotion.agentsmanagement.dao.AgentCodeRepository;
import com.betmotion.agentsmanagement.domain.AgentCode;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class AgentCodeService {

  AgentCodeRepository agentCodeRepository;


  @Transactional(propagation = REQUIRES_NEW)
  public AgentCode generateCode() {
    AgentCode agentCode = new AgentCode();
    String code = random(10, true, true);
    agentCode.setCode(code);
    agentCodeRepository.save(agentCode);
    return agentCode;
  }

}
