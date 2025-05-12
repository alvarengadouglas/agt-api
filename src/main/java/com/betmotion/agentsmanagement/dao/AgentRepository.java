package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentRepository extends BaseJpaRepository<Agent, Integer> {

  //Use it only for testing
  @Query("from Agent  where user.userName =:userName")
  Agent findByUserName(@Param("userName") String userName);

  @Query("from Agent  where user.id in (:userIds)")
  List<Agent> findByUserIds(@Param("userIds") Set<Integer> userIds);

  @Query(value = "from Agent  where id =:id"
      + " and user.status = com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE")
  Optional<Agent> findActiveAgentById(@Param(value = "id") Integer id);

  @Query(value = "from Agent as agent"
      + " where agent.user.status = com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE")
  Page<Agent> findActiveAgents(Pageable pageRequest);

  @Query("from Agent a join fetch a.user where a.id = :id")
  Agent findByIdFetchUser(Integer id);

  @Query(value = "select count(*) from Agent as agent"
      + " where agent.parentId =:parentId")
  Integer countAgentsByParentId(@Param("parentId") Integer parentId);

  @Query(value = "from Agent as agent"
      + " where agent.parentId =:parentId "
      + "and  agent.user.status = com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE")
  Page<Agent> findActiveAgentsChildAgents(Pageable pageRequest,
      @Param("parentId") Integer parentId);

  @Query(value = "from Agent as agent"
      + " where agent.user.userName like :text "
      + "and  agent.user.status = com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE")
  Page<Agent> findActiveAgentsByText(Pageable pageRequest, @Param("text") String text);

  Page<Agent> findAllByParentId(Pageable pageable, Integer parentId);

  @Query(nativeQuery = true, 
    value = "with t as ( "
      + "    select user_id, id  "
      + "    from agents  "
      + "    where parent_agent_id = :parentAgentId  "
      + "    union all  "
      + "    select agents.user_id, agents.id  "
      + "    from agents  "
      + "             inner join t on t.id = agents.parent_agent_id  "
      + ")  "
      + "select t.id  "
      + "from t "+
            " join users u on t.user_id = u.id where u.status != 'CLOSED'")
  List<Integer> findAllSubAgentsByParentAgent(Integer parentAgentId);

  @Query("from Agent a join fetch a.user where charindex(:id, a.parentTree) > 0 and a.id <> :agentId")
  List<Agent> findAllAgentTree(String id, Integer agentId);

  @Query("select a.id from Agent a where a.user.role = :userRole")
  List<Integer> findAllIdsOfAgentsByRole(@Param("userRole") UserRole userRole);

  List<Agent> findByUserRole(@Param("userRole") UserRole userRole);

  @Query("select count(*) from Agent a where a.user.role = :userRole")
  Long countByRole(@Param("userRole") UserRole userRole);

  Agent findByUserId(Integer userId);

  @Query("select a.id from Agent a where a.permissionUnblock = 1")
  List<Integer> findAllIdsOfAgentsHasPermissionUnblock();

}
