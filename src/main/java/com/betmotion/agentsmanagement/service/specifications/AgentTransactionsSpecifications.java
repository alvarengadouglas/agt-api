package com.betmotion.agentsmanagement.service.specifications;


import static com.betmotion.agentsmanagement.domain.apt.AgentTransactionDomainModel.OPERATION_DATE;
import static com.betmotion.agentsmanagement.domain.apt.AgentTransactionDomainModel.OPERATION_TYPE;
import static com.betmotion.agentsmanagement.domain.apt.AgentTransactionDomainModel.TARGETUSER;

import com.betmotion.agentsmanagement.domain.AgentTransaction;
import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.Path;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class AgentTransactionsSpecifications {

  public static Specification<AgentTransaction> hasTargetUsers(List<User> users) {
    return (transaction, cq, cb) -> {
      Path<User> targetUsers = transaction.get(TARGETUSER);
      return targetUsers.in(users);
    };
  }

  public static Specification<AgentTransaction> startFrom(LocalDateTime startFrom) {
    return (transaction, cq, cb) -> {
      Path<LocalDateTime> startFromPath = transaction.get(OPERATION_DATE);
      return cb.greaterThan(startFromPath, startFrom);
    };
  }

  public static Specification<AgentTransaction> endTo(LocalDateTime endTo) {
    return (transaction, cq, cb) -> {
      Path<LocalDateTime> startFromPath = transaction.get(OPERATION_DATE);
      return cb.lessThan(startFromPath, endTo);
    };
  }

  public static Specification<AgentTransaction> hasTransactionTypes(
      List<AgentTransactionType> transactionTypes) {
    return (transaction, cq, cb) -> {
      Path<AgentTransactionType> playerIdPath = transaction.get(OPERATION_TYPE);
      return playerIdPath.in(transactionTypes);
    };
  }

}
