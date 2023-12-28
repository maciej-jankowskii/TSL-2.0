package com.tsl.service;

import com.tsl.model.role.EmployeeRole;
import com.tsl.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.tsl.model.employee.User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        return new User(user.getEmail(), user.getPassword(), mapRoles(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRoles(List<EmployeeRole> roleList) {
        return roleList.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
}
