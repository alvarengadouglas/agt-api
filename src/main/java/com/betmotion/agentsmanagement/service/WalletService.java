package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.WalletRepository;
import com.betmotion.agentsmanagement.domain.Wallet;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class WalletService {

  WalletRepository walletRepository;

  @Transactional
  public Wallet createWallet(Boolean isCreditsWallet) {
    Wallet wallet = new Wallet();
    wallet.setBalance(0L);
    wallet.setIsCreditsWallet(isCreditsWallet);
    walletRepository.saveAndFlush(wallet);
    return wallet;
  }
}
