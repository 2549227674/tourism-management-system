package com.tourism.service;

import com.tourism.dto.LoginRequest;
import com.tourism.dto.LoginResponse;
import com.tourism.dto.RegisterRequest;
import com.tourism.dto.UserDTO;

public interface UserService {

    UserDTO register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserDTO getUserById(Long id);
}
