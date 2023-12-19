package com.tsl.controller;

import com.tsl.dtos.AuthResponseDTO;
import com.tsl.dtos.UserLoginDTO;
import com.tsl.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
            String token = authService.loginUser(loginDTO);
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
    }
}
