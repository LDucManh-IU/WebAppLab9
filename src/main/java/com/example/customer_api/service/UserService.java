package com.example.customer_api.service;

import java.util.List;

import com.example.customer_api.dto.ChangePasswordDTO;
import com.example.customer_api.dto.LoginRequestDTO;
import com.example.customer_api.dto.LoginResponseDTO;
import com.example.customer_api.dto.RegisterRequestDTO;
import com.example.customer_api.dto.ResetPasswordDTO;
import com.example.customer_api.dto.UpdateProfileDTO;
import com.example.customer_api.dto.UpdateRoleDTO;
import com.example.customer_api.dto.UserResponseDTO;
import com.example.customer_api.entity.RefreshToken;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);
    
    void changePassword(String username, ChangePasswordDTO changePasswordDTO);
    
    String generatePasswordResetToken(String email);
    
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    
    // Exercise 7
    UserResponseDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO);
    
    void deleteAccount(String username, String password);
    
    // Exercise 8
    List<UserResponseDTO> getAllUsers();
    
    UserResponseDTO updateUserRole(Long userId, UpdateRoleDTO updateRoleDTO);
    
    UserResponseDTO toggleUserStatus(Long userId);
    
    // Exercise 9
    RefreshToken createRefreshToken(String username);
    
    LoginResponseDTO refreshAccessToken(String refreshToken);
}
