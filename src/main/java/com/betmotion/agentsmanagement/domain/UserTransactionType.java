package com.betmotion.agentsmanagement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public enum UserTransactionType {

  //Means credits movement to user wallet in platform
  DEPOSIT(1),

  //Means credit movement from platform to agent credits wallet

  WITHDRAWAL(-1),
  //Means real money movement from player to agent real wallet

  PAYMENT(-1),
  //Means real money movement from agent wallet to player hands

  PAYOUT(1),

  //Means credits movement from user wallet to agent hands
  DEPOSIT_ROLLBACK(-1),

  //Means real money movement from agent wallet to player hands
  WITHDRAWAL_ROLLBACK(1),

  //Means real money movement from player to agent real wallet
  PAYMENT_ROLLBACK(1),

  //Means real money movement from agent wallet to player hands
  PAYOUT_ROLLBACK(-1),;
  long reportAmountMutiplier;
}
