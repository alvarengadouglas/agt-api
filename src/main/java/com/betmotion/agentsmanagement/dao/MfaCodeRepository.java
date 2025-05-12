package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.MfaCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface MfaCodeRepository extends BaseJpaRepository<MfaCode, Long> {

    @Query("from MfaCode mc where mc.user.id = :userId and mc.code = :code and mc.expiresAt > :expiresAt")
    Optional<MfaCode> findValidCodeByUserId(Integer userId, String code, Date expiresAt);

    void deleteByUserId(Integer userId);
}
