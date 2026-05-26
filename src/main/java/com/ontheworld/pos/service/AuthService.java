package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.auth.LoginRequest;
import com.ontheworld.pos.dto.auth.LoginResponse;
import com.ontheworld.pos.dto.auth.MemberRegisterRequest;
import com.ontheworld.pos.dto.auth.UserSummary;

public interface AuthService {
    LoginResponse authenticate(LoginRequest request);
    LoginResponse register(MemberRegisterRequest request);
    LoginResponse refresh(String refreshToken);
    void logout(String refreshToken);
    UserSummary getCurrentUser(String username);
}
