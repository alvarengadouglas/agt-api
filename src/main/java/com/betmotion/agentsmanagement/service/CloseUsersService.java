package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.dao.UserRepository;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CloseUsersService {

    @Autowired
    PlatformApi platformApi;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AgentService agentService;

    @Autowired
    SemaphoreControlService semaphoreControlService;

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 3 * * *")
    public void closeAllUsers() throws Exception {
        boolean result = semaphoreControlService.tryGenericLock(RedisKey.SCHEDULED_CLOSE_ALL_USERS, "");
        if (result) {
            log.info("INIT CLOSE ALL USERS");
            List<String> userAndIdToCloseDTOS = platformApi.getAllInactiveUsers();
            List<String> userAndIdToCloseSentDTOS = new ArrayList<>();
            try {
                if (!userAndIdToCloseDTOS.isEmpty()) {
                    int count = 0;
                    for (String username : userAndIdToCloseDTOS) {

                        Optional<User> userOpt = userRepository.findByUserNameAndStatusNot(username, UserStatus.CLOSED);
                        Optional<Player> playerOpt = playerRepository.findByUserNameAndStatusNot(username, UserStatus.CLOSED);

                        if (userOpt.isPresent() && playerOpt.isPresent()) {
                            User user = userOpt.get();
                            Player player = playerOpt.get();
                            if (user.getRole().equals(UserRole.PLAYER)) {
                                userAndIdToCloseSentDTOS.add(username);

                                user.setStatus(UserStatus.CLOSED);
                                user.setReceiveEmail(Boolean.FALSE);
                                player.setStatus(UserStatus.CLOSED);

                                userRepository.save(user);
                                playerRepository.save(player);
                                count++;
                            }
                        }
                        if (count == 10 || userAndIdToCloseDTOS.get(userAndIdToCloseDTOS.size() - 1).equals(username)) {
                            log.info("SEND CLOSED USERS TO API");
                            count = 0;
                            platformApi.closeInactiveUsers(userAndIdToCloseSentDTOS);
                            userAndIdToCloseSentDTOS = new ArrayList<>();
                        }
                    }
                }
                log.info("FINISH CLOSE ALL USERS");
            } catch (Exception e) {
                log.info("ERROR CLOSE ALL USERS");
                e.printStackTrace();
                throw new Exception();
            }
        }
        semaphoreControlService.genericUnLock(RedisKey.SCHEDULED_CLOSE_ALL_USERS, "");
    }

    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 2 * * 1")
    public void closeAllUsersInactive() throws Exception {

        boolean result = semaphoreControlService.tryGenericLock(RedisKey.SCHEDULED_CLOSE_ALL_USERS_INACTIVE, "");
        if (result) {
            log.info("INIT CLOSE ALL USERS INACTIVE");
            agentService.deactivationAgents();
        }
        semaphoreControlService.genericUnLock(RedisKey.SCHEDULED_CLOSE_ALL_USERS_INACTIVE, "");
    }

}
