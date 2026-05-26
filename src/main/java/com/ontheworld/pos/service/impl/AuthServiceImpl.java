package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.auth.LoginRequest;
import com.ontheworld.pos.dto.auth.LoginResponse;
import com.ontheworld.pos.dto.auth.MemberRegisterRequest;
import com.ontheworld.pos.dto.auth.UserSummary;
import com.ontheworld.pos.entity.Customer;
import com.ontheworld.pos.entity.RefreshToken;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.repository.CustomerRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
import com.ontheworld.pos.security.CustomUserDetails;
import com.ontheworld.pos.security.JwtTokenProvider;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.AuthService;
import com.ontheworld.pos.service.RefreshTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           RefreshTokenService refreshTokenService,
                           UserAccountRepository userAccountRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userAccountRepository = userAccountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount userAccount = userDetails.getUserAccount();
        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.create(userAccount);
        auditService.log("User", userAccount.getId().toString(), "LOGIN",
                "User logged in: " + userAccount.getUsername(), userAccount.getUsername());
        return new LoginResponse(accessToken, refreshToken.getToken(),
                tokenProvider.getAccessTokenExpirationMinutes(), new UserSummary(userAccount));
    }

    @Override
    @Transactional
    public LoginResponse register(MemberRegisterRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        String nameEn = (request.getNameEn() != null && !request.getNameEn().isBlank())
                ? request.getNameEn() : request.getNameTh();
        Customer customer = customerRepository.save(
                new Customer(request.getNameTh(), nameEn, request.getPhone(), request.getEmail()));

        UserAccount user = new UserAccount(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                Role.MEMBER,
                null
        );
        user.setCustomer(customer);
        user = userAccountRepository.save(user);

        String accessToken = tokenProvider.generateTokenFromUser(user);
        RefreshToken refreshToken = refreshTokenService.create(user);
        auditService.log("User", user.getId().toString(), "REGISTER",
                "New member registered: " + user.getUsername(), user.getUsername());
        return new LoginResponse(accessToken, refreshToken.getToken(),
                tokenProvider.getAccessTokenExpirationMinutes(), new UserSummary(user));
    }

    @Override
    public LoginResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.validateAndGet(token);
        UserAccount user = refreshToken.getUser();
        if (!user.isEnabled()) {
            refreshTokenService.deleteAllByUser(user);
            throw new DisabledException("User account is disabled");
        }
        refreshTokenService.deleteByToken(token);
        String accessToken = tokenProvider.generateTokenFromUser(user);
        RefreshToken newRefreshToken = refreshTokenService.create(user);
        return new LoginResponse(accessToken, newRefreshToken.getToken(),
                tokenProvider.getAccessTokenExpirationMinutes(), new UserSummary(user));
    }

    @Override
    public void logout(String token) {
        refreshTokenService.deleteByToken(token);
    }

    @Override
    public UserSummary getCurrentUser(String username) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        return new UserSummary(user);
    }
}
