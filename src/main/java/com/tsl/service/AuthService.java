package com.tsl.service;

import com.tsl.config.jwt.JWTGenerator;
import com.tsl.dtos.UserLoginDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTGenerator jwtGenerator;

    public AuthService(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
    }

    public String loginUser(UserLoginDTO loginDTO) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = jwtGenerator.generatedToken(authenticate);
            return token;
        } catch (AuthenticationException exception){
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}
