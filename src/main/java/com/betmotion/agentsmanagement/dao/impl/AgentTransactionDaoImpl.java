package com.betmotion.agentsmanagement.dao.impl;

import org.springframework.stereotype.Repository;

@Repository
public class AgentTransactionDaoImpl {


    public String getAgentTransactionsByTargetAgentIdDateAgentTransactionType() {
        return  "Select operation_date as operationDate, u.user_name as userName, operation_type as agentTransactionType, amount * CASE operation_type " +
                " WHEN 'DEPOSIT' THEN 1 " +
                " WHEN 'WITHDRAWAL' THEN -1 " +
                " WHEN 'DEPOSIT_ROLLBACK' THEN -1 " +
                " WHEN 'WITHDRAWAL_ROLLBACK' THEN 1 " +
                " ELSE 1 "+
                " END as amount, " +
                " COALESCE(bonus,0) as bonus " +
                "from agent_transaction at2  " +
                "join users u on at2.source_user_id = u.id  " +
                "join users ut on at2.target_user_id  = ut.id  " +
                "join agents a on ut.id  = a.user_id " +
                "where a.id = :agentId " +
                "and at2.operation_date between :startDate and :endDate " +
                "and at2.operation_type in (:operationType) " +
                " order by at2.id desc " +
                " offset :offset rows fetch next :pageSize rows only ";
    }

    public String getCountAgentTransactionsByTargetAgentIdDateAgentTransactionType() {
        return "Select count(at2.id) " +
                "from agent_transaction at2  " +
                "join users u on at2.source_user_id = u.id  " +
                "join users ut on at2.target_user_id  = ut.id  " +
                "join agents a on ut.id  = a.user_id " +
                "where a.id = :agentId " +
                "and at2.operation_date between :startDate and :endDate " +
                "and at2.operation_type in (:operationType) ";
    }
    
    public String getSumAtentTransactionsByTargetAgentIdDateAgentTransactionType() {
        return "Select  sum((amount + COALESCE(bonus,0)) * CASE operation_type " +
                "                 WHEN 'DEPOSIT' THEN 1 " +
                "                 WHEN 'WITHDRAWAL' THEN -1 " +
                "                 WHEN 'DEPOSIT_ROLLBACK' THEN -1 " +
                "                 WHEN 'WITHDRAWAL_ROLLBACK' THEN 1 " +
                "                 ELSE 1 " +
                "                 END) as total, " +
                "       sum(amount * CASE operation_type " +
                "                 WHEN 'DEPOSIT' THEN 1 " +
                "                 WHEN 'WITHDRAWAL' THEN -1 " +
                "                 WHEN 'DEPOSIT_ROLLBACK' THEN -1 " +
                "                 WHEN 'WITHDRAWAL_ROLLBACK' THEN 1 " +
                "                 ELSE 1 " +
                "                 END) as amount, " +
                "       sum(COALESCE(bonus,0)) as bonus " +
                "from " +
                " agent_transaction at2 " +
                "join users u on " +
                " at2.source_user_id = u.id " +
                "join users ut on " +
                " at2.target_user_id = ut.id " +
                "join agents a on " +
                " ut.id = a.user_id " +
                "where " +
                " a.id = :agentId " +
                " and at2.operation_date between :startDate and :endDate " +
                " and at2.operation_type in (:operationType)";
    }
}
