package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.UserSessionDTO;

public interface SessionService {
    void saveSession(String username, UserSessionDTO session);
    UserSessionDTO getSession(String username);
    void deleteSession(String username);
}
