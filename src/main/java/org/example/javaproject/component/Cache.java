package org.example.javaproject.component;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class Cache {
    private final CustomLogger logger = new CustomLogger();
    private final Map<String, Object> hashMap;
    private static final int MAX_SIZE = 10;

    public Cache() {
        this.hashMap = new LinkedHashMap<>();
    }

    public void put(String key, Object value) {
        hashMap.put(key, value);
        logger.cachePut();
        if (hashMap.size() > MAX_SIZE) {
            String oldestKey = hashMap.keySet().iterator().next();
            hashMap.remove(oldestKey);
        }
    }

    public Object get(String key) {
        return hashMap.get(key);
    }

    public void remove(String key) {
        hashMap.remove(key);
        logger.cacheRemove();
    }

    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }

    public void clear() {
        hashMap.clear();
        logger.info("Cache cleared");
    }

}
