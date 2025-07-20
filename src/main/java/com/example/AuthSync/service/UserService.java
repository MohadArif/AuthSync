package com.example.AuthSync.service;

import com.example.AuthSync.dto.UserEntityRequest;
import com.example.AuthSync.dto.UserEntityResponse;

public interface UserService {
    UserEntityResponse createUser(UserEntityRequest userEntityRequest);

    UserEntityResponse getUserProfile(String email);

    void sendRestOtp(String email);
    void resetPassword(String email,String otp,String newPassword);

    void sendOtp(String email);

    void verifyOtp(String email,String otp);

    String getloggedInUser(String email);
}
