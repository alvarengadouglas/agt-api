package com.betmotion.agentsmanagement.dao;

import com.betmotion.agentsmanagement.dao.core.BaseJpaRepository;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.UserStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlayerRepository extends BaseJpaRepository<Player, Integer> {

  Player findByPlatformId(Integer platformId);

  Player findByUserName(String userName);

  @Query("from Player where userName in (:userNames)")
  List<Player> findByUserNames(@Param("userNames") Set<String> userNames);

  Optional<Player> findByUserNameAndStatusNot(String userName, UserStatus status);

  @Query("select p.platformId from Player p where p.id = :id")
  Optional<Integer> findPlatformIdById(Integer id);


  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update Player set status = :status where userId in (:userIds)")
  void changeStatus(@Param("userIds") List<Integer> userIds, @Param("status") UserStatus status);

  Player findByUserId(Integer userId);
}
