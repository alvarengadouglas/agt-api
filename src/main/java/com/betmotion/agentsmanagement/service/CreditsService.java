package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.WalletRepository;
import com.betmotion.agentsmanagement.domain.CreditWalletAware;
import com.betmotion.agentsmanagement.domain.Wallet;
import com.betmotion.agentsmanagement.rest.dto.CreditBalanceDto;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class CreditsService {


  UserProvider userProvider;

  AgentRepository agentRepository;

  WalletRepository walletRepository;

  @Transactional
  public CreditBalanceDto getCreditBalance() {
    CreditBalanceDto result = new CreditBalanceDto();
    result.setAmount(getCreditWalletForCurrentUser().getBalance());
    return result;
  }

  private CreditWalletAware getCurrentCreditWallet() {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    Integer agentId = ((AppUserDetails) currentUserDetails).getId();
    return agentRepository.getEntity(agentId);
  }

  @Transactional
  public Wallet getCreditWalletForCurrentUser() {
    Integer creditWalletId = getCurrentCreditWallet().getCreditWalletId();
    return walletRepository.findAndLockById(creditWalletId);
  }
}
