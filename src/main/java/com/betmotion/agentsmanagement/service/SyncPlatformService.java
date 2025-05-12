package com.betmotion.agentsmanagement.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import com.betmotion.agentsmanagement.platform.api.dto.FindByIdsRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.betmotion.agentsmanagement.platform.api.dto.UserStatus;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class SyncPlatformService {

  PlatformApi platformApi;

  PlayerRepository playerRepository;

  @Transactional
  public void syncFromPlatform() {
    List<Player> allPlayers = playerRepository.findAll();
    if (!CollectionUtils.isEmpty(allPlayers)) {
      Set<Integer> platformIds = allPlayers.stream().map(Player::getPlatformId).collect(toSet());
      FindByIdsRequestDto request = new FindByIdsRequestDto();
      request.setUserIds(platformIds);
      List<PlatformUserDto> byIds = platformApi.findByIds(request);
      Map<Integer, Player> playersByPlatformIds = allPlayers.stream().collect(toMap(
          Player::getPlatformId, identity()));
      byIds.forEach(platformUser ->
          assignDataFromPlatformToPlayer(platformUser, playersByPlatformIds));
      playerRepository.saveAll(playersByPlatformIds.values());
    }
  }

  private void assignDataFromPlatformToPlayer(PlatformUserDto platformUser,
      Map<Integer, Player> playersByPlatformIds) {
    Player player = playersByPlatformIds.get(platformUser.getId());
    if (player != null) {
      String statusInPlatform = platformUser.getStatus();
      if (UserStatus.ACTIVE.name().equals(statusInPlatform)) {
        player.setStatus(com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE);
      } else if (UserStatus.BLOCKED.name().equals(statusInPlatform)) {
        player.setStatus(com.betmotion.agentsmanagement.domain.UserStatus.BLOCKED);
      }
    }
  }

}
