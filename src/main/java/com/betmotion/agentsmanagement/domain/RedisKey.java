package com.betmotion.agentsmanagement.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public enum RedisKey {
    TRANSACTION_USER() {
        @Override
        public String getRedisKey(String extraKey) {
            return String.format("%s_tu_%s", REDIS_PREFIX, extraKey);
        }
    },
    CURRENT_USER_DETAILS() {
        @Override
        public String getRedisKey(String extraKey) {
            return String.format("%s_cud_%s", REDIS_PREFIX, extraKey);
        }
    },
    SCHEDULED_CLOSE_ALL_USERS() {
        @Override
        public String getRedisKey(String extraKey) {
            return String.format("%s_scau_%s", REDIS_PREFIX, extraKey);
        }
    },
    SCHEDULED_CLOSE_ALL_USERS_INACTIVE() {
        @Override
        public String getRedisKey(String extraKey) {
            return String.format("%s_scaui_%s", REDIS_PREFIX, extraKey);
        }
    };
    @Value("${application.redis.prefix}")
    private static String REDIS_PREFIX;
    public abstract String getRedisKey(String extraKey);
}
