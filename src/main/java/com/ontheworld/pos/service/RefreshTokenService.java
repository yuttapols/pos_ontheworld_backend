package com.ontheworld.pos.service;

import com.ontheworld.pos.entity.RefreshToken;
import com.ontheworld.pos.entity.UserAccount;

public interface RefreshTokenService {
    RefreshToken create(UserAccount user);
    RefreshToken validateAndGet(String token);
    void deleteByToken(String token);
    void deleteAllByUser(UserAccount user);
}
