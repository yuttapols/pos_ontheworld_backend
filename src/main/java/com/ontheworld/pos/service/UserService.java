package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.user.ChangePasswordRequest;
import com.ontheworld.pos.dto.user.UserRequest;
import com.ontheworld.pos.dto.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUser(UUID id);
    List<UserResponse> listUsers();
    UserResponse updateUser(UUID id, UserRequest request);
    void deleteUser(UUID id, String performedBy);
    void changePassword(String username, ChangePasswordRequest request);
}
