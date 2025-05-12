package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.PlayerWallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PlayerWalletRepository extends BaseJpaRepository<PlayerWallet, Integer> {


  @Query("from PlayerWallet where playerId = :playerId")
  @Transactional
  PlayerWallet findAndLockByPlayerId(@Param("playerId") Integer playerId);

  @Query("delete from PlayerWallet where playerId = :playerId")
  void deleteByPlayer(@Param("playerId") Integer playerId);

}
