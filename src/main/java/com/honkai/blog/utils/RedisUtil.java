
package com.honkai.blog.utils;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Slf4j
public class RedisUtil {
    private static final String DELIMIT = ":";

    private final StringRedisTemplate stringRedisTemplate;

    private final ValueOperations<String, String> ops;

    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.ops = stringRedisTemplate.opsForValue();
    }

    
    public boolean set(final String key, Object value, Long expire) {
        boolean result = false;
        if (expire == null) {
            log.error("Redis set error! ExpireTime can not be null! key: {}, value: {}", key, value.toString());
            return false;
        }
        try {
            String v;
            Class<?> clazz = value.getClass();
            if (clazz.equals(String.class) || clazz.equals(StringBuffer.class) || clazz.equals(StringBuilder.class)) {
                v = value.toString();
            } else {
                v = Jackson.toJsonString(value);
            }
            // 负数过期时间则永不过期
            if (expire > 0L) {
                ops.set(key, v, expire, TimeUnit.MILLISECONDS);
            } else {
                ops.set(key, v);
            }
            result = true;
        } catch (Exception e) {
            log.error("Redis set error! Key: {}, Value: {}", key, value, e);
        }
        return result;
    }

    public boolean set(String bucket, String key, Object value, Long expire) {
        return set(String.join(DELIMIT, bucket, key), value, expire);
    }

    public boolean setWithoutUpdateTtl(final String key, Object value) {
        long expire = -2L;
        final long expireTime = -2L;
        boolean result = false;
        try {
            Long expireLong = ops.getOperations().getExpire(key);
            expire = expireLong != null ? expireLong : 0L;
        } catch (Exception e) {
            log.error("Redis set error! Key:{}, Value:{}", key, value.toString(), e);
            return false;
        }
        if (expire == expireTime) {
            log.error("当前Key不存在! Key: {}", key);
        } else {
            result = set(key, value, expire);
        }
        return result;
    }

    public boolean setWithoutUpdateTtl(String bucket, String key, Object value) {
        return setWithoutUpdateTtl(String.join(DELIMIT, bucket, key), value);
    }

    
    public <T> T get(final String key, Jackson.BaseType<T> type) {
        T value = null;
        String res = ops.get(key);
        if (StringUtils.hasText(res)) {
            try {
                value = Jackson.parse(res, type);
            } catch (Exception e) {
                Optional.ofNullable(e.getCause())
                        .map(Throwable::getMessage)
                        .filter(m -> m.contains("no Creators, like default constructor, exist"))
                        .ifPresent(message -> {
                            log.error("反序列化失败，请检查类型中是否所有对象都存在\"无参\"构造器!");
                        });
                log.error("Redis get error! Key: {}, Type: {}", key, type.getType().getTypeName(), e);
            }
        }
        return value;
    }

    public <T> T get(String bucket, String key, Jackson.BaseType<T> type) {
        return get(String.join(DELIMIT, bucket, key), type);
    }

    
    public <T> T get(final String key, Class<T> clazz) {
        T value = null;
        String res = ops.get(key);
        if (StringUtils.hasText(res)) {
            try {
                value = Jackson.parse(res, clazz);
            } catch (Exception e) {
                Optional.ofNullable(e.getCause())
                        .map(Throwable::getMessage)
                        .filter(m -> m.contains("no Creators, like default constructor, exist"))
                        .ifPresent(message -> {
                            log.error("反序列化失败，请检查类型中是否所有对象都存在\"无参\"构造器!");
                        });
                log.error("Redis get error! Key: {} Class: {}", key, clazz.getName(), e);
            }
        }
        return value;
    }

    public <T> T get(String bucket, String key, Class<T> clazz) {
        return get(String.join(DELIMIT, bucket, key), clazz);
    }

    
    public String get(final String key) {
        String value = null;
        try {
            value = ops.get(key);
        } catch (Exception e) {
            log.error("Redis get error! Key: {} Class: {}", key, String.class.getName(), e);
        }
        return value;
    }

    public String get(String bucket, String key) {
        return get(String.join(DELIMIT, bucket, key));
    }

    public boolean remove(String key) {
        boolean result = false;
        try {
            result = Boolean.TRUE.equals(stringRedisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis remove error! Key: {}", key, e);
        }
        return result;
    }

    public boolean remove(String bucket, String key) {
        return remove(String.join(DELIMIT, bucket, key));
    }

    public boolean containsKey(String key) {
        return ops.get(key) != null;
    }

    public boolean containsKey(String bucket, String key) {
        return containsKey(String.join(DELIMIT, bucket, key));
    }

    public String generateKey(String bucket, String key) {
        return String.join(DELIMIT, bucket, key);
    }

    public Long increment(String key) {
        Long result = null;
        try {
            result = ops.increment(key);
        } catch (Exception e) {
            log.error("Redis increment error! Key: {}", key, e);
        }
        return result;
    }

    public Long increment(String bucket, String key) {
        return increment(generateKey(bucket, key));
    }

    public Map<String, String> getAllWithBucket(@NotNull String bucket) {
        Map<String, String> result = null;
        try {
            List<String> keys = new ArrayList<>(Objects.requireNonNull(stringRedisTemplate.keys(bucket + "*")));
            List<String> values = ops.multiGet(keys);
            result = keys.stream().collect(Collectors.toMap(key -> key, key -> {
                assert values != null;
                if (values != null) {
                    return values.get(keys.indexOf(key));
                } else {
                    return null;
                }
            }));
        } catch (Exception e) {
            log.error("Redis get all error! Bucket: {}", bucket, e);
        }
        return result;
    }

    public Integer getCountWithBucket(String bucket) {
        int count = -1;
        try {
            count = Objects.requireNonNull(stringRedisTemplate.keys(bucket + "*")).size();
        } catch (NullPointerException e) {
            log.error("There are no keys with bucket! Bucket: {}", bucket, e);
        }
        return count;
    }
}