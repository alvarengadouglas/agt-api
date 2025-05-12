package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.AgentTransactionRepository;
import com.betmotion.agentsmanagement.domain.AgentTransaction;
import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class AgentTransactionService {

  AgentTransactionRepository agentTransactionRepository;

  @Transactional
  public void createTransaction(User sourceUser, User targetUser, Long amount,
      AgentTransactionType transactionType, String note, Long balance, Long bonus) {
    AgentTransaction result = new AgentTransaction();
    result.setAmount(amount);
    result.setNote(note);
    result.setBalance(balance);
    result.setOperationType(transactionType);
    result.setOperationDate(LocalDateTime.now());
    result.setSourceUser(sourceUser);
    result.setTargetUser(targetUser);
    result.setBonus(bonus);
    agentTransactionRepository.save(result);
  }

}
