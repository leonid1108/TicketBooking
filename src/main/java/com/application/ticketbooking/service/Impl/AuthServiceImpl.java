package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.RegisterResponse;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.exception.BadRequestException;
import com.application.ticketbooking.exception.EntityNotFoundException;
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

/**
 * Реализация сервиса аутентификации и регистрации пользователей.
 * <p>
 * Обеспечивает регистрацию новых пользователей и их аутентификацию.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenManager jwtTokenManager;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param user объект {@link User}, содержащий данные пользователя для регистрации
     * @return {@link RegisterResponse} с информацией о зарегистрированном пользователе
     * @throws BadRequestException если пользователь с таким именем уже существует
     */
    @Override
    public RegisterResponse registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }

        User createdUser = new User();
        createdUser.setUsername(user.getUsername());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        createdUser.setRole(user.getRole());

        if (user.getRole() == null || user.getRole().isBlank()) {
            createdUser.setRole("ROLE_USER");
        } else {
            createdUser.setRole(user.getRole());
        }
        createdUser.setEnabled(true);

        User savedUser = userRepository.save(createdUser);
        RegisterResponse registerResponse = modelMapper.map(savedUser, RegisterResponse.class);
        registerResponse.setMessage("Пользователь успешно зарегистрирован.");
        return registerResponse;
    }

    /**
     * Аутентифицирует пользователя и генерирует JWT-токен.
     *
     * @param authRequest объект запроса с учетными данными пользователя
     * @return {@link AuthResponse} с JWT-токеном, именем пользователя и его ролью
     * @throws EntityNotFoundException если пользователь не найден
     * @throws BadRequestException если введен неверный пароль
     */
    @Override
    public AuthResponse loginUser(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(
                        () -> new EntityNotFoundException("Пользователь не найден.")
                );

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Неверный пароль");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUsername());

        return new AuthResponse(jwtTokenManager.generateJwtToken(userDetails), user.getUsername(), user.getRole());
    }


}
