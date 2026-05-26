package com.ontheworld.pos.service.impl;

import com.ontheworld.pos.dto.user.ChangePasswordRequest;
import com.ontheworld.pos.dto.user.UserRequest;
import com.ontheworld.pos.dto.user.UserResponse;
import com.ontheworld.pos.entity.Branch;
import com.ontheworld.pos.entity.Role;
import com.ontheworld.pos.entity.UserAccount;
import com.ontheworld.pos.exception.BadRequestException;
import com.ontheworld.pos.exception.EntityNotFoundException;
import com.ontheworld.pos.mapper.UserMapper;
import com.ontheworld.pos.repository.BranchRepository;
import com.ontheworld.pos.repository.UserAccountRepository;
import com.ontheworld.pos.service.AuditService;
import com.ontheworld.pos.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userRepository;
    private final BranchRepository branchRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserServiceImpl(UserAccountRepository userRepository,
                           BranchRepository branchRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.branchRepository = branchRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required when creating a user");
        }

        Branch branch = resolveBranch(request.getBranchId());
        Role role = resolveRole(request.getRole());

        UserAccount user = new UserAccount(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                role,
                branch
        );
        user.setEnabled(request.isEnabled());
        UserAccount saved = userRepository.save(user);
        auditService.log("User", saved.getId().toString(), "CREATE",
                "Created user " + saved.getUsername() + " with role " + saved.getRole(), currentUsername());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUser(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    @Override
    public List<UserResponse> listUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(resolveRole(request.getRole()));
        user.setBranch(resolveBranch(request.getBranchId()));
        user.setEnabled(request.isEnabled());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UserAccount saved = userRepository.save(user);
        auditService.log("User", id.toString(), "UPDATE",
                "Updated user " + saved.getUsername(), currentUsername());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id, String performedBy) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        user.softDelete(performedBy);
        userRepository.save(user);
        auditService.log("User", id.toString(), "DELETE",
                "Soft deleted user " + user.getUsername(), performedBy);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        auditService.log("User", user.getId().toString(), "CHANGE_PASSWORD",
                "Changed password for user " + username, username);
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    private Branch resolveBranch(UUID branchId) {
        if (branchId == null) return null;
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new EntityNotFoundException("Branch not found: " + branchId));
    }

    private Role resolveRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role + ". Valid values: ADMIN, MANAGER, CASHIER, MEMBER");
        }
    }
}
