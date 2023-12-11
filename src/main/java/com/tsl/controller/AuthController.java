package com.tsl.controller;

import com.tsl.dtos.AuthResponseDTO;
import com.tsl.dtos.UserLoginDTO;
import com.tsl.repository.UserRepository;
import com.tsl.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
        try {
            String token = authService.loginUser(loginDTO);
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }
}
