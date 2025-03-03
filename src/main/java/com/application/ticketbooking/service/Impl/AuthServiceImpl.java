package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.UserResponse;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.UserRepository;
import com.application.ticketbooking.service.AuthService;
import com.application.ticketbooking.token.JwtTokenManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public UserResponse registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        User createdUser = new User();
        createdUser.setUsername(user.getUsername());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setRole(user.getRole());

        User savedUser = userRepository.save(createdUser);
        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        userResponse.setMessage("Пользователь успешно зарегистрирован.");
        return userResponse;
    }

    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(
                        () -> new RuntimeException("Пользователь не найден.")
                );

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUsername());

        return new AuthResponse(jwtTokenManager.generateJwtToken(userDetails), user.getUsername(), user.getRole());
    }


}
