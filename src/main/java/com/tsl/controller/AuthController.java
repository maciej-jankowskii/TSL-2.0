package com.tsl.controller;

import com.tsl.model.employee.*;
import com.tsl.repository.UserRepository;
import com.tsl.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserLoginDTO loginDTO){
        String token = authService.loginUser(loginDTO);
        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);

    }

    @GetMapping("/allForwarders")
    public ResponseEntity<List<User>> finall(){
        Iterable<User> allForwarders = userRepository.findAll();
        return ResponseEntity.ok((List<User>) allForwarders);

    }



}
