package com.cybersoft.minibank.service;

public interface RedisService {

    void setLock(String key, long timeoutMinutes);

    boolean isLocked(String key);

    void save(String key, String value);

    String get(String key);

    void delete(String key);
}
