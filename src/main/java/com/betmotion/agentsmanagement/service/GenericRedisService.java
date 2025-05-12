package com.betmotion.agentsmanagement.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@Service
public class GenericRedisService<T> {

    @Autowired
    RedisTemplate<String, T> redisTemplate;

    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void save(String key, T value) {
        try {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(String key, T value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Optional<T> findByKey(String key) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.delete(key);
    }

    public void delete(List<String> keys) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.delete(keys);
    }

    public List<String> findKeys(String pattern) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return new ArrayList<>(redisTemplate.keys(pattern));
    }

    public <G> Optional<G> findByKeyAndConvert(String redisKey, Class<G> type) {
        try {
            Optional<T> optionalGeneric = findByKey(redisKey);
            if (optionalGeneric.isPresent()) {
                G genericObject = mapper.readValue((String) optionalGeneric.get(), mapper.getTypeFactory().constructType(type));
                return Optional.of(genericObject);
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public <G> List<G> findByKeyAndConvertList(String redisKey, Class<G> type) {
        try {
            Optional<T> optionalGeneric = findByKey(redisKey);

            if (optionalGeneric.isPresent()) {
                List<G> genericObject = mapper.readValue((String) optionalGeneric.get(), mapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
                return genericObject;
            }
            return new ArrayList<G>();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void convertAndSave(String redisKey, Object object) {
        try {
            String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            save(redisKey, (T) jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void convertAndSave(String redisKey, Object object, long timeout, TimeUnit unit) {
        try {
            String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            save(redisKey, (T) jsonResult, timeout, unit);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
