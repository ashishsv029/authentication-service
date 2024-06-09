package com.meghasandesham.authenticationservice.service;

import com.meghasandesham.authenticationservice.dto.*;

public interface AuthService {
    RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) throws Exception;
    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws Exception;
    ValidationResponseDTO validateUser(ValidationRequestDTO validateRequestDTO) throws Exception;

}
