package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.RegisterResponse;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.exception.BadRequestException;
import com.application.ticketbooking.exception.EntityNotFoundException;
import com.application.ticketbooking.repository.UserRepository;
import com.application.ticketbooking.service.Impl.AuthServiceImpl;
import com.application.ticketbooking.service.Impl.CustomUserDetailsService;
import com.application.ticketbooking.token.JwtTokenManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование работы класса сервиса AuthServiceImpl")
@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenManager jwtTokenManager;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private User savedUser;
    private RegisterResponse registerResponse;
    private AuthRequest authRequest;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setRole("ROLE_ADMIN");

        savedUser = new User();
        savedUser.setUsername("testUser");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole("ROLE_ADMIN");
        savedUser.setEnabled(true);

        registerResponse = new RegisterResponse();
        registerResponse.setUsername("testUser");
        registerResponse.setRole("ROLE_ADMIN");
        registerResponse.setMessage("Пользователь успешно зарегистрирован.");

        authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password");

        authentication = new UsernamePasswordAuthenticationToken("testUser", "password");

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testUser")
                .password("encodedPassword")
                .roles("ADMIN")
                .build();
    }

    @Test
    @DisplayName("Успешная регистрация пользователя")
    void registerUser_success() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, RegisterResponse.class)).thenReturn(registerResponse);

        RegisterResponse result = authService.registerUser(user);

        assertEquals(registerResponse, result);
        verify(userRepository).findByUsername(user.getUsername());
        verify(passwordEncoder).encode(user.getPassword());
        verify(userRepository).save(any(User.class));
        verify(modelMapper).map(savedUser, RegisterResponse.class);
    }

    @Test
    @DisplayName("Регистрация пользователя, который уже существует")
    void registerUser_userAlreadyExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> authService.registerUser(user));

        verify(userRepository).findByUsername(user.getUsername());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(modelMapper, never()).map(any(User.class), eq(RegisterResponse.class));
    }

    @Test
    @DisplayName("Регистрация пользователя без указания роли")
    void registerUser_defaultRole() {
        user.setRole(null);
        savedUser.setRole("ROLE_USER");
        registerResponse.setRole("ROLE_USER");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, RegisterResponse.class)).thenReturn(registerResponse);

        RegisterResponse result = authService.registerUser(user);

        assertEquals(registerResponse, result);
        assertEquals("ROLE_USER", result.getRole());
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    void loginUser_success() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(customUserDetailsService.loadUserByUsername(authRequest.getUsername())).thenReturn(userDetails);
        when(jwtTokenManager.generateJwtToken(userDetails)).thenReturn("testToken");

        AuthResponse result = authService.loginUser(authRequest);

        assertEquals("testToken", result.getToken());
        assertEquals("testUser", result.getUsername());
        assertEquals("ROLE_ADMIN", result.getRole());

        verify(userRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), user.getPassword());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService).loadUserByUsername(authRequest.getUsername());
        verify(jwtTokenManager).generateJwtToken(userDetails);
    }

    @Test
    @DisplayName("Авторизация не существующего пользователя или пользователя с неверным именем")
    void loginUser_userNotFound() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.loginUser(authRequest));

        verify(userRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenManager, never()).generateJwtToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным паролем")
    void loginUser_wrongPassword() {
        when(userRepository.findByUsername(authRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(authRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.loginUser(authRequest));

        verify(userRepository).findByUsername(authRequest.getUsername());
        verify(passwordEncoder).matches(authRequest.getPassword(), user.getPassword());
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenManager, never()).generateJwtToken(any(UserDetails.class));
    }
}