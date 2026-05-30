package com.ontheworld.pos.controller.v1;

import com.ontheworld.pos.dto.user.ChangePasswordRequest;
import com.ontheworld.pos.dto.user.UserRequest;
import com.ontheworld.pos.dto.user.UserResponse;
import com.ontheworld.pos.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Create a new user (ADMIN: any role/branch | BRANCH_ADMIN: MANAGER/CASHIER in their branch)")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request,
                                               @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(userService.createUser(request, caller.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "List users (scoped by caller role: ADMIN=all, BRANCH_ADMIN=branch employees, MANAGER=cashiers, CASHIER=self)")
    public ResponseEntity<List<UserResponse>> list(@AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(userService.listUsers(caller.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_ADMIN', 'MANAGER', 'CASHIER')")
    @Operation(summary = "Get user by ID (scoped by caller role)")
    public ResponseEntity<UserResponse> get(@PathVariable UUID id,
                                            @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(userService.getUser(id, caller.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Update a user (ADMIN: any | BRANCH_ADMIN: MANAGER/CASHIER in their branch)")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
                                               @Valid @RequestBody UserRequest request,
                                               @AuthenticationPrincipal UserDetails caller) {
        return ResponseEntity.ok(userService.updateUser(id, request, caller.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BRANCH_ADMIN')")
    @Operation(summary = "Soft delete a user (ADMIN: any | BRANCH_ADMIN: MANAGER/CASHIER in their branch)")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @AuthenticationPrincipal UserDetails caller) {
        userService.deleteUser(id, caller.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password for the currently logged-in user")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               @AuthenticationPrincipal UserDetails caller) {
        userService.changePassword(caller.getUsername(), request);
        return ResponseEntity.noContent().build();
    }
}
