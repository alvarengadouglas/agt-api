package com.betmotion.agentsmanagement.service;

import static com.betmotion.agentsmanagement.domain.UserRole.PLAYER;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.UserStatus;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserInfoEnricher {

  PlayerRepository playerRepository;

  PlatformApiService platformApiService;

  public void enrichDataWithPlatformData(List<UserInfoDto> usersInfoList) {
    if (!isEmpty(usersInfoList)) {
      List<UserInfoDto> onlyPlayers = usersInfoList.stream()
          .filter(item -> PLAYER.equals(item.getRole()))
          .collect(toList());
      if (!isEmpty(onlyPlayers)) {
        Set<String> userNames = onlyPlayers
            .stream()
            .map(UserInfoDto::getUserName)
            .collect(toSet());
        List<Player> players = playerRepository.findByUserNames(userNames);
        if (!isEmpty(players)) {
          List<PlatformUserDto> platformData = platformApiService.getPlatformUserDtos(players);
          if (!isEmpty(platformData)) {
            platformData.removeIf(user -> UserStatus.valueOf(user.getStatus()).equals(UserStatus.CLOSED));
            Map<String, PlatformUserDto> mappedData = platformData.stream().collect(
                Collectors.toMap(item -> item.getUserName().toLowerCase(), identity()));
            onlyPlayers.forEach(player -> {
              PlatformUserDto dateInPlatform = mappedData.get(player.getUserName().toLowerCase());
              if (dateInPlatform != null) {
                player.setStatus(dateInPlatform.getStatus());
                player.setCredits(dateInPlatform.getAmount());
              }
            });
          }
        }
      }
    }
  }

}
