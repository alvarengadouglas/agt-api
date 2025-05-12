package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.domain.RedisKey;
import com.betmotion.agentsmanagement.service.exceptions.SemaphoreControlNumberOfAttemptsException;

import com.betmotion.agentsmanagement.utils.DateFormatUtils;
import lombok.extern.slf4j.Slf4j;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.betmotion.agentsmanagement.domain.RedisKey.TRANSACTION_USER;

@Service
@Slf4j
public class SemaphoreControlService {

    private static final Integer ONE_HUNDRED = 100;
    private static final Integer FIVE_MINUTES = 300000;
    private static final Integer NUMBER_OF_ATTEMPTS = 600;
    private static final Integer TWO_HOUR = 2;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GenericRedisService<String> redisService;

    public void tryLock(Integer userId, Integer count) throws SemaphoreControlNumberOfAttemptsException {
        RLock rLock = this.redissonClient.getLock(TRANSACTION_USER.getRedisKey(String.valueOf(userId)));
        try {
            boolean isLock = rLock.tryLock(ONE_HUNDRED, FIVE_MINUTES, TimeUnit.MILLISECONDS);
            if (!isLock) {
                log.info(String.format("LOCK TRANSACTION - userId: %s - Time: %s", userId
                        , DateFormatUtils.format(DateFormatUtils.DATE_TIME_FORMAT_WITH_MILLISECONDS_GMT_UTC, OffsetDateTime.now())));

                if (count >= NUMBER_OF_ATTEMPTS) {
                    log.info(String.format("LOCK TRANSACTION - RuntimeException - userId: %s", userId));
                    Stream.of(Thread.currentThread().getStackTrace())
                            .filter(trace -> (trace.getClassName().contains("com.ags.") ||
                                    trace.getClassName().contains("com.salsa.")) && !trace.getClassName().contains("SemaphoreControlServiceImpl"))
                            .forEach(trace -> {
                                log.info(String.format("LOCK TRANSACTION - class: %s - method: %s - userId: %s", trace.getClassName(), trace.getMethodName(), userId));
                            });
                    throw new SemaphoreControlNumberOfAttemptsException();
                }
                tryLock(userId, ++count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            try {
                rLock.unlock();
            } catch (Exception ex) {
                //redisService.delete(TRANSACTION_USER.getRedisKey(String.valueOf(userId)));
                log.info(TRANSACTION_USER.getRedisKey(String.valueOf(userId)) + " redis key deleted manually when InterruptedException");
            }
        }

    }

    public void unlock(Integer userId) {
        try {
            RLock rLock = redissonClient.getLock(TRANSACTION_USER.getRedisKey(String.valueOf(userId)));
            if (rLock.isLocked()) {
                rLock.unlock();
            }
        } catch (Exception e) {
            //redisService.delete(TRANSACTION_USER.getRedisKey(String.valueOf(userId)));
            log.info(TRANSACTION_USER.getRedisKey(String.valueOf(userId)) + " redis key deleted manually");
        }
    }

    public boolean tryGenericLock(RedisKey key, String extraKey) {
        RLock rLock = this.redissonClient.getLock(key.getRedisKey(extraKey));
        try {
            boolean isLock = rLock.tryLock(0, TWO_HOUR, TimeUnit.HOURS);
            return isLock;
        } catch (InterruptedException e) {
            e.printStackTrace();
            try {
                rLock.unlock();
            } catch (Exception ex) {
                //redisService.delete(key.getRedisKey(extraKey));
                log.info(key.getRedisKey(extraKey) + " redis key deleted manually when InterruptedException");
            }
        }
        return false;
    }

    public void genericUnLock(RedisKey key, String extraKey) {
        try {
            RLock rLock = redissonClient.getLock(key.getRedisKey(extraKey));
            if (rLock.isLocked()) {
                rLock.unlock();
            }
        } catch (Exception e) {
            //redisService.delete(key.getRedisKey(extraKey));
            log.info(key.getRedisKey(extraKey) + " redis key deleted manually");
        }
    }

    public boolean genericIsLock(RedisKey key, String extraKey) {
        RLock rLock = redissonClient.getLock(key.getRedisKey(extraKey));
        return rLock.isLocked();
    }
}
