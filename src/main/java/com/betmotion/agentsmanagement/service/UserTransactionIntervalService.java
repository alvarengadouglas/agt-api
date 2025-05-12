package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.UserTransactionIntervalRepository;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.rest.dto.user.UserTypeIntervalEnum;
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
public class UserTransactionIntervalService {

  UserTransactionIntervalRepository userTransactionIntervalRepository;


  @Transactional
  public void saveTransactionUser(Integer userPlayerId, UserTransactionType type, LocalDateTime transactionDate, UserTypeIntervalEnum typeUser) {

    Optional<UserTransactionInterval> userEntity = userTransactionIntervalRepository.findByUserPlayerIdAndAndUserType(userPlayerId, typeUser);

    if (userEntity.isPresent()) {
      userEntity.get().setOperationDate(transactionDate);
      userEntity.get().setOperationType(type);
      userTransactionIntervalRepository.save(userEntity.get());
    } else {
      UserTransactionInterval userTransactionInterval = new UserTransactionInterval();
      userTransactionInterval.setUserPlayerId(userPlayerId);
      userTransactionInterval.setOperationType(type);
      userTransactionInterval.setUserType(typeUser);
      userTransactionInterval.setOperationDate(transactionDate);
      userTransactionIntervalRepository.saveAndFlush(userTransactionInterval);
    }
  }
}
