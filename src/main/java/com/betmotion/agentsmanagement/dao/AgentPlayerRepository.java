package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.AgentPlayer;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentPlayerRepository extends BaseJpaRepository<AgentPlayer, Integer> {

  AgentPlayer findByPlayerId(Integer playerId);

  @Query("delete from AgentPlayer where playerId = :playerId")
  void deleteByPlayer(@Param("playerId") Integer playerId);

  @Query("from AgentPlayer where playerId in (:playerIds)")
  List<AgentPlayer> findByPlayerId(@Param("playerIds") Set<Integer> playerIds);

  @Query(value = "select count(*) from AgentPlayer where agentId = :agentId")
  Integer countPlayersByAgentId(@Param("agentId") Integer agentId);

}
