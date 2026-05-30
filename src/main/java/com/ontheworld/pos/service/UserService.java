package com.ontheworld.pos.service;

import com.ontheworld.pos.dto.user.ChangePasswordRequest;
import com.ontheworld.pos.dto.user.UserRequest;
import com.ontheworld.pos.dto.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRequest request, String callerUsername);
    UserResponse getUser(UUID id, String callerUsername);
    List<UserResponse> listUsers(String callerUsername);
    UserResponse updateUser(UUID id, UserRequest request, String callerUsername);
    void deleteUser(UUID id, String performedBy);
    void changePassword(String username, ChangePasswordRequest request);
}
