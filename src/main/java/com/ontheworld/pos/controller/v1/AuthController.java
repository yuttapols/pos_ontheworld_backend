package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.auth.LoginRequest;
import com.ontheworld.pos.dto.auth.LoginResponse;
import com.ontheworld.pos.dto.auth.MemberRegisterRequest;
import com.ontheworld.pos.dto.auth.RefreshTokenRequest;
import com.ontheworld.pos.dto.auth.UserSummary;
import com.ontheworld.pos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get access + refresh token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register as a new member")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Get new access token using refresh token")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current logged-in user info")
    public ResponseEntity<UserSummary> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(authService.getCurrentUser(user.getUsername()));
    }
}
