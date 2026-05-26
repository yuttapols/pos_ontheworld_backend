package com.ontheworld.pos.dto.auth;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType = "Bearer";
    private final long accessTokenExpiresInMinutes;
    private final UserSummary user;

    public LoginResponse(String accessToken, String refreshToken,
                         long accessTokenExpiresInMinutes, UserSummary user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresInMinutes = accessTokenExpiresInMinutes;
        this.user = user;
    }
}
