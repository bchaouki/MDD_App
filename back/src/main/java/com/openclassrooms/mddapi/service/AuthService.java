package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import jakarta.validation.Valid;

public interface AuthService {

    AuthResponse register(@Valid RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
