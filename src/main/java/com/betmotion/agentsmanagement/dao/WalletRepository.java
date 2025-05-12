package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.Wallet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends BaseJpaRepository<Wallet, Integer> {


  @Query(" from Wallet where id = :id")
  Wallet findAndLockById(@Param("id") Integer id);
}
