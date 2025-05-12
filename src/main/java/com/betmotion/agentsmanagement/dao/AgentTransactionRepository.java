package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.SumJpaRepository;
import com.betmotion.agentsmanagement.domain.AgentTransaction;
import com.betmotion.agentsmanagement.domain.AgentTransactionType;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentTransactionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public interface AgentTransactionRepository extends JpaRepository<AgentTransaction, Integer>,
    JpaSpecificationExecutor<AgentTransaction>, SumJpaRepository<AgentTransaction> {

}
