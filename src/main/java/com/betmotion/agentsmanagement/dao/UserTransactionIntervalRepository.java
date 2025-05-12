package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.dao.core.SumJpaRepository;
import com.betmotion.agentsmanagement.domain.UserTransactionInterval;
import com.betmotion.agentsmanagement.rest.dto.user.UserTypeIntervalEnum;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserTransactionIntervalRepository extends BaseJpaRepository<UserTransactionInterval, Integer>,
    JpaSpecificationExecutor<UserTransactionInterval>, SumJpaRepository<UserTransactionInterval> {

    Optional<UserTransactionInterval> findByUserPlayerIdAndAndUserType(Integer userId, UserTypeIntervalEnum typeUser);
}
