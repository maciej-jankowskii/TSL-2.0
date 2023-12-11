package com.tsl.controller;

import com.tsl.dtos.AuthResponseDTO;
import com.tsl.dtos.UserLoginDTO;
import com.tsl.repository.UserRepository;
import com.tsl.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserLoginDTO loginDTO) {
        String token = authService.loginUser(loginDTO);
        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);

    }
}
