package com.tsl.config;

import com.tsl.config.jwt.JWTAuthEntryPoint;
import com.tsl.config.jwt.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JWTAuthEntryPoint authEntryPoint;

    public SecurityConfig(JWTAuthEntryPoint authEntryPoint) {
        this.authEntryPoint = authEntryPoint;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/contact/**").permitAll()
                .requestMatchers("/warehouses/**", "/cargos/**").hasAnyRole("ADMIN", "FORWARDER", "PLANNER")
                .requestMatchers("/customers/**", "/carriers/**").hasAnyRole("ADMIN", "FORWARDER", "PLANNER", "ACCOUNTANT")
                .requestMatchers("/forwarding-orders").hasAnyRole("FORWARDER", "ADMIN")
                .requestMatchers("/invoices/**").hasAnyRole("ACCOUNTANT", "ADMIN")
                .requestMatchers("/transport-orders").hasAnyRole("PLANNER", "ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/salary/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

}
