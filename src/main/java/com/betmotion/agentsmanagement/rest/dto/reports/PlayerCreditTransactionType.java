package com.betmotion.agentsmanagement.rest.dto.reports;

import com.betmotion.agentsmanagement.domain.UserTransactionType;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum PlayerCreditTransactionType {

  ADD_CREDITS(UserTransactionType.DEPOSIT),

  WITHDRAWAL_CREDITS(UserTransactionType.WITHDRAWAL),

  PAYMENT_PLAYER(UserTransactionType.PAYMENT),

  PAYOUT_PLAYER(UserTransactionType.PAYOUT),

  ADD_CREDITS_ROLLBACK(UserTransactionType.DEPOSIT_ROLLBACK),

  WITHDRAWAL_CREDITS_ROLLBACK(UserTransactionType.WITHDRAWAL_ROLLBACK),

  PAYMENT_PLAYER_ROLLBACK(UserTransactionType.PAYMENT_ROLLBACK),

  PAYOUT_PLAYER_ROLLBACK(UserTransactionType.PAYOUT_ROLLBACK);

  UserTransactionType transactionType;

  public static PlayerCreditTransactionType findByUserTransactionType(UserTransactionType value) {
    return Arrays.stream(PlayerCreditTransactionType.values())
        .filter(item -> item.getTransactionType().equals(value))
        .findFirst()
        .orElseThrow(IllegalStateException::new);
  }
}