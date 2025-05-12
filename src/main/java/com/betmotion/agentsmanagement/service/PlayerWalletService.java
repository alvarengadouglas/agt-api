package com.betmotion.agentsmanagement.service;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.PlayerWalletRepository;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.PlayerWallet;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class PlayerWalletService {

  PlayerWalletRepository playerWalletRepository;

  @Transactional
  public void createWalletForPlayer(Player player) {
    PlayerWallet playerWallet = new PlayerWallet();
    playerWallet.setPlayerId(player.getId());
    playerWallet.setPlatformBalance(0L);
    playerWallet.setBalance(0L);
    playerWalletRepository.save(playerWallet);
  }

  @Transactional
  public void deleteByPlayer(Integer playerId) {
    playerWalletRepository.deleteByPlayer(playerId);
  }

}
