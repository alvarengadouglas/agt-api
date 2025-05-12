package com.betmotion.agentsmanagement.service.specifications;

import com.betmotion.agentsmanagement.domain.UserTransaction;
import com.betmotion.agentsmanagement.domain.UserTransactionStatus;
import com.betmotion.agentsmanagement.domain.UserTransactionType;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.Path;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import static com.betmotion.agentsmanagement.domain.apt.UserTransactionDomainModel.*;

@UtilityClass
public class UserTransactionsSpecifications {

  public static Specification<UserTransaction> hasPlayers(List<Long> playerIds) {
    return (transaction, cq, cb) -> {
      Path<Integer> playerIdPath = transaction.get(PLAYERID);
      return playerIdPath.in(playerIds);
    };
  }

  public static Specification<UserTransaction> startFrom(LocalDateTime startFrom) {
    return (transaction, cq, cb) -> {
      Path<LocalDateTime> startFromPath = transaction.get(OPERATION_DATE);
      return cb.greaterThan(startFromPath, startFrom);
    };
  }

  public static Specification<UserTransaction> endTo(LocalDateTime endTo) {
    return (transaction, cq, cb) -> {
      Path<LocalDateTime> startFromPath = transaction.get(OPERATION_DATE);
      return cb.lessThan(startFromPath, endTo);
    };
  }

  public static Specification<UserTransaction> hasTransactionTypes(
      List<UserTransactionType> transactionTypes) {
    return (transaction, cq, cb) -> {
      Path<UserTransactionType> playerIdPath = transaction.get(OPERATION_TYPE);
      return playerIdPath.in(transactionTypes);
    };
  }

  public static Specification<UserTransaction> hasTransactionStatus(
          UserTransactionStatus userTransactionStatus) {
    return (transaction, cq, cb) -> {
      Path<UserTransactionType> playerIdPath = transaction.get(TRANSACTION_STATUS);
      return playerIdPath.in(userTransactionStatus);
    };
  }

}
