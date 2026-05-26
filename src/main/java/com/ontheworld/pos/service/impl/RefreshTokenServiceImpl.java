package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.entity.RefreshToken;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.TokenException;
import com.ontheworld.pos.repository.RefreshTokenRepository;
import com.ontheworld.pos.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh-token-expiration-minutes}")
    private long refreshTokenExpirationMinutes;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public RefreshToken create(UserAccount user) {
        RefreshToken refreshToken = new RefreshToken(
                UUID.randomUUID().toString(),
                user,
                LocalDateTime.now().plusMinutes(refreshTokenExpirationMinutes)
        );
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken validateAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("Refresh token not found"));
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException("Refresh token expired — please login again");
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    @Transactional
    public void deleteAllByUser(UserAccount user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
