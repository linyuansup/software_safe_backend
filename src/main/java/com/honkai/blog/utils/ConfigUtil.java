
package com.honkai.blog.utils;

import com.honkai.blog.db.entity.Config;
import com.honkai.blog.db.mapper.ConfigMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
public class ConfigUtil {

    @Resource
    ConfigMapper configMapper;

    private static final Map<String, Config> CONFIG_MAP = new ConcurrentHashMap<>();

    public void init() {
        CONFIG_MAP.clear();
        configMapper.selectList(null).forEach(config -> {
            CONFIG_MAP.put(config.getKey(), config);
            log.info("加载配置 {}: {}", config.getKey(), config.getValue());
        });
    }

    public String get(String key) {
        Config config = CONFIG_MAP.get(key);
        return config == null ? null : config.getValue();
    }

    public <T> T get(String key, Class<T> clazz) {
        String value = get(key);
        return value == null ? null : Jackson.parse(value, clazz);
    }

    public Config getConfig(String key) {
        return CONFIG_MAP.get(key);
    }

    public List<Config> getAll() {
        return new ArrayList<>(CONFIG_MAP.values());
    }

    public boolean update(Config config) {
        if (configMapper.updateById(config) == 1) {
            CONFIG_MAP.put(config.getKey(), config);
            return true;
        }
        return false;
    }

    public boolean add(Config config) {
        if (configMapper.insert(config) == 1) {
            CONFIG_MAP.put(config.getKey(), config);
            return true;
        }
        return false;
    }

    public boolean delete(String configKey) {
        if (configMapper.deleteById(configKey) == 1) {
            CONFIG_MAP.remove(configKey);
            return true;
        }
        return false;
    }
}
