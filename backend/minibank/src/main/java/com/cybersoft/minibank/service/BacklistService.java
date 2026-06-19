package com.cybersoft.minibank.service;

public interface BacklistService {
    void blacklistToken(String token, long minutes);
    boolean isBlacklisted(String token);
}
