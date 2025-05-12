package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum AgentRestTransactionType {

  ADD_SALDO(AgentTransactionType.ADD_SALDO),

  REMOVE_SALDO(AgentTransactionType.REMOVE_SALDO),

  DEPOST_CREDIT(AgentTransactionType.DEPOSIT),

  WITHDRAW_CREDIT(AgentTransactionType.WITHDRAWAL),

  DEPOST_CREDIT_ROLLBACK(AgentTransactionType.DEPOSIT_ROLLBACK),

  WITHDRAW_CREDIT_ROLLBACK(AgentTransactionType.WITHDRAWAL_ROLLBACK);

  AgentTransactionType transactionType;

  public static AgentRestTransactionType findByAgentTransactionType(
      AgentTransactionType value) {
    return Arrays.stream(AgentRestTransactionType.values())
        .filter(item -> item.getTransactionType().equals(value))
        .findFirst()
        .orElseThrow(IllegalStateException::new);
  }
}