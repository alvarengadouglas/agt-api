package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.WalletTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WalletTransactionRepository extends BaseJpaRepository<WalletTransaction, Integer>,
    JpaSpecificationExecutor<WalletTransaction> {

  List<WalletTransaction> findByWalletId(Integer walletId);
}