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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
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
    public UserResponse createUser(UserRequest request, String callerUsername) {
        UserAccount caller = requireUser(callerUsername);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required when creating a user");
        }

        Role role = resolveRole(request.getRole());
        Branch branch = resolveBranchForCreate(caller, role, request.getBranchId());

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
                "Created user " + saved.getUsername() + " with role " + saved.getRole(), callerUsername);
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUser(UUID id, String callerUsername) {
        UserAccount caller = requireUser(callerUsername);
        UserAccount target = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        assertCanView(caller, target);
        return userMapper.toResponse(target);
    }

    @Override
    public List<UserResponse> listUsers(String callerUsername) {
        UserAccount caller = requireUser(callerUsername);
        return switch (caller.getRole()) {
            case ADMIN -> userMapper.toResponseList(userRepository.findAll());
            case BRANCH_ADMIN -> {
                requireBranch(caller);
                yield userMapper.toResponseList(
                        userRepository.findByBranchAndRoleInAndDeletedAtIsNull(
                                caller.getBranch(), Set.of(Role.MANAGER, Role.CASHIER)));
            }
            case MANAGER -> {
                requireBranch(caller);
                yield userMapper.toResponseList(
                        userRepository.findByBranchAndRoleInAndDeletedAtIsNull(
                                caller.getBranch(), Set.of(Role.CASHIER)));
            }
            default -> List.of(userMapper.toResponse(caller));
        };
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request, String callerUsername) {
        UserAccount caller = requireUser(callerUsername);
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));

        assertCanManage(caller, user);

        if (!user.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        if (!user.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        Role role = resolveRole(request.getRole());
        Branch branch = resolveBranchForCreate(caller, role, request.getBranchId());

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(role);
        user.setBranch(branch);
        user.setEnabled(request.isEnabled());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        UserAccount saved = userRepository.save(user);
        auditService.log("User", id.toString(), "UPDATE",
                "Updated user " + saved.getUsername(), callerUsername);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id, String performedBy) {
        UserAccount caller = requireUser(performedBy);
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
        assertCanManage(caller, user);
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

    // ── helpers ──────────────────────────────────────────────────────────────

    private UserAccount requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    private void requireBranch(UserAccount user) {
        if (user.getBranch() == null) {
            throw new BadRequestException("User has no branch assigned");
        }
    }

    /** Scoped visibility check: can 'caller' view 'target'? */
    private void assertCanView(UserAccount caller, UserAccount target) {
        switch (caller.getRole()) {
            case ADMIN -> { /* full access */ }
            case BRANCH_ADMIN -> {
                requireBranch(caller);
                if (!sameBranch(caller, target) || isAdminLevel(target)) {
                    throw new AccessDeniedException("Access denied");
                }
            }
            case MANAGER -> {
                requireBranch(caller);
                if (!sameBranch(caller, target) || target.getRole() != Role.CASHIER) {
                    throw new AccessDeniedException("Access denied");
                }
            }
            default -> {
                if (!caller.getId().equals(target.getId())) {
                    throw new AccessDeniedException("Access denied");
                }
            }
        }
    }

    /** Scoped management check: can 'caller' create/update/delete 'target'? */
    private void assertCanManage(UserAccount caller, UserAccount target) {
        switch (caller.getRole()) {
            case ADMIN -> { /* full access */ }
            case BRANCH_ADMIN -> {
                requireBranch(caller);
                if (!sameBranch(caller, target) || isAdminLevel(target)) {
                    throw new AccessDeniedException("Branch admin cannot manage users outside their branch or admin-level users");
                }
            }
            default -> throw new AccessDeniedException("Insufficient permission to manage users");
        }
    }

    /**
     * Determine branch for create/update.
     * ADMIN: uses request.branchId (nullable for ADMIN/MEMBER).
     * BRANCH_ADMIN: forced to their own branch; can only assign MANAGER/CASHIER.
     */
    private Branch resolveBranchForCreate(UserAccount caller, Role targetRole, UUID requestedBranchId) {
        if (caller.getRole() == Role.BRANCH_ADMIN) {
            if (targetRole == Role.ADMIN || targetRole == Role.BRANCH_ADMIN || targetRole == Role.MEMBER) {
                throw new AccessDeniedException("Branch admin can only create MANAGER or CASHIER");
            }
            return caller.getBranch();
        }
        // ADMIN path
        if (targetRole != Role.ADMIN && targetRole != Role.MEMBER && requestedBranchId == null) {
            throw new BadRequestException("Branch ID is required for role: " + targetRole);
        }
        return resolveBranch(requestedBranchId);
    }

    private boolean sameBranch(UserAccount a, UserAccount b) {
        return a.getBranch() != null && b.getBranch() != null
                && a.getBranch().getId().equals(b.getBranch().getId());
    }

    private boolean isAdminLevel(UserAccount user) {
        return user.getRole() == Role.ADMIN || user.getRole() == Role.BRANCH_ADMIN;
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
            throw new BadRequestException("Invalid role: " + role
                    + ". Valid values: ADMIN, BRANCH_ADMIN, MANAGER, CASHIER, MEMBER");
        }
    }
}
