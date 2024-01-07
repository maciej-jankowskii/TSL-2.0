package com.tsl.service;

import com.tsl.config.jwt.JWTGenerator;
import com.tsl.dtos.UserLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JWTGenerator jwtGenerator;
    @InjectMocks private AuthService authService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should log in correctly")
    public void testLoginUser_Success(){
        UserLoginDTO userLoginDTO = prepareDataForLogIn();

        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = "exampleToken";
        when(jwtGenerator.generatedToken(authentication)).thenReturn(token);

        String resultToken = authService.loginUser(userLoginDTO);

        assertEquals(token, resultToken);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtGenerator, times(1)).generatedToken(authentication);
        verifyNoMoreInteractions(authenticationManager, jwtGenerator);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException because login data is wrong")
    public void testLoginUser_withInvalidData(){
        UserLoginDTO userLoginDTO = prepareDataForLogIn();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.loginUser(userLoginDTO));
    }

    private static UserLoginDTO prepareDataForLogIn() {
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password");
        return loginDTO;
    }
}