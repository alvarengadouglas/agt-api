package com.betmotion.agentsmanagement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public enum AgentTransactionType {

  //Increase debt of agent to parent
  ADD_SALDO(1),

  //Decrease debt of agent to parent
  REMOVE_SALDO(-1),

  //add credits to agent
  DEPOSIT(1),

  //Remove credits from agent
  WITHDRAWAL(-1),

  //add credits to agent
  DEPOSIT_ROLLBACK(-1),

  //Remove credits from agent
  WITHDRAWAL_ROLLBACK(1);

  long reportAmountMutiplier;

  public static List<String> getDepositWithdrawToFilter() {
    return List.of(DEPOSIT.name(),
            WITHDRAWAL.name(),
            DEPOSIT_ROLLBACK.name(),
            WITHDRAWAL_ROLLBACK.name());
  }


}
