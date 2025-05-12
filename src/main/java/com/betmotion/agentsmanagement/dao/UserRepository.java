package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.User;
import java.util.List;
import java.util.Optional;
import com.betmotion.agentsmanagement.domain.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends BaseJpaRepository<User, Integer> {

  User findByUserName(String userName);

  List<User> findByEmail(String email);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update User set agent = :agent where ((id in (:userIds)) and (agent is null))")
  void linkUsersToAgent(@Param("userIds") List<Integer> userIds, @Param("agent") Agent agent);


  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update User set status = :status where id in (:userIds)")
  void changeStatus(@Param("userIds") List<Integer> userIds, @Param("status") UserStatus status);

  @Query(nativeQuery = true,
          value = "SELECT u.* " +
                  "FROM users u " +
                  "JOIN agents a ON u.id = a.user_id " +
                  "WHERE a.parent_tree LIKE CONCAT('%', :agentId, '%') " +

                  "UNION ALL " +

                  "SELECT u.* " +
                  "FROM users u " +
                  "JOIN players p ON u.id = p.user_id " +
                  "JOIN agents_players ap ON p.id = ap.players_id " +
                  "JOIN agents a ON a.id = ap.agent_id " +
                  "WHERE a.parent_tree LIKE CONCAT('%', :agentId, '%') and u.status != 'CLOSED' ")
  List<User> findAllUserToChanceStatus(@Param("agentId") String agentId);

  Optional<User> findByUserNameAndStatusNot(String userName, UserStatus status);

}