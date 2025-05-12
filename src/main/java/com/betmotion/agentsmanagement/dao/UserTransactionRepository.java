package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.dao.core.SumJpaRepository;
import com.betmotion.agentsmanagement.domain.UserTransaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserTransactionRepository extends BaseJpaRepository<UserTransaction, Integer>,
    JpaSpecificationExecutor<UserTransaction>, SumJpaRepository<UserTransaction> {

}
