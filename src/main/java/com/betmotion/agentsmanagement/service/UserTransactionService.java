package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.UserTransactionRepository;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.queue.dto.TransactionConfirmStatusDto;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserTransactionService {

  UserTransactionRepository userTransactionRepository;

  AgentPlayerRepository agentPlayerRepository;

  @Transactional
  public UserTransaction create(Agent agent, Integer playerId, UserTransactionType type,
      Long amount, Long balance, String note, LocalDateTime transactionDate, Long bonus) {
    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(playerId);
    UserTransaction userTransaction = new UserTransaction();
    userTransaction.setUserid(agent.getUserId());
    userTransaction.setDirectPlayerAgentId(agentPlayer.getAgent().getUserId());
    userTransaction.setPlayerid(playerId);
    userTransaction.setBonus(bonus);
    userTransaction.setAmount(amount);
    userTransaction.setOperationType(type);
    userTransaction.setOperationDate(transactionDate);
    userTransaction.setBalance(balance);
    userTransaction.setNote(note);
    userTransactionRepository.saveAndFlush(userTransaction);
    return userTransaction;
  }

  @Transactional
  public void update(UserTransaction userTransaction) {
    userTransactionRepository.saveAndFlush(userTransaction);
  }

  public Optional<UserTransaction> confirmTransaction(TransactionConfirmStatusDto transactionConfirmStatusDto) {
    Optional<UserTransaction> userTransactionOptional = userTransactionRepository.findById(transactionConfirmStatusDto.getRemoteId().intValue());
    if(userTransactionOptional.isEmpty()){
      log.error("Transaction not found: {}", transactionConfirmStatusDto);
      return userTransactionOptional;
    }

    UserTransaction userTransaction = userTransactionOptional.get();
    userTransaction.setTransactionStatus(transactionConfirmStatusDto.getStatus());
    userTransactionRepository.save(userTransaction);

    if (transactionConfirmStatusDto.getChargedRemoteId() != null) {
      Optional<UserTransaction> optTransactionRelated = userTransactionRepository.findById(transactionConfirmStatusDto.getChargedRemoteId().intValue());
      optTransactionRelated.ifPresent(transactionRelated -> {
        transactionRelated.setTransactionStatus(transactionConfirmStatusDto.getStatus());
        update(transactionRelated);
      });
    }

    return userTransactionOptional;
  }

  public Optional<UserTransaction> findById(Integer id) {
    return userTransactionRepository.findById(id);
  }
}
