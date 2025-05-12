package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.WalletTransactionRepository;
import com.betmotion.agentsmanagement.domain.WalletTransaction;
import com.betmotion.agentsmanagement.domain.WalletTransactionType;
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
public class WalletTransactionService {

  WalletTransactionRepository walletTransactionRepository;

  @Transactional
  public WalletTransaction saveTransaction(Integer walletId, Long amount,
      WalletTransactionType type, Long balance) {
    return saveTransaction(walletId, amount, type, "", balance);
  }

  @Transactional
  public WalletTransaction saveTransaction(Integer walletId, Long amount,
      WalletTransactionType type, String note, Long balance) {
    WalletTransaction result = new WalletTransaction();
    result.setWalletId(walletId);
    result.setAmount(amount);
    result.setOperationType(type);
    result.setOperationDate(LocalDateTime.now());
    result.setNote(note);
    result.setBalance(balance);
    walletTransactionRepository.saveAndFlush(result);
    return result;
  }
}
