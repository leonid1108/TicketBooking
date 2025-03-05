package com.application.ticketbooking.controller;

import com.application.ticketbooking.controller.api.AuthApi;
import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.RegisterResponse;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов, связанных с аутентификацией и регистрацией пользователей.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param user объект пользователя, содержащий регистрационные данные.
     * @return {@link ResponseEntity} с {@link RegisterResponse}, содержащим информацию о зарегистрированном пользователе.
     */
    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody User user) {
        RegisterResponse registerResponse = authService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param authRequest объект {@link AuthRequest}, содержащий учетные данные пользователя.
     * @return {@link ResponseEntity} с {@link AuthResponse}, содержащим JWT-токен или другую информацию о сеансе.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.loginUser(authRequest);
        return ResponseEntity.ok(authResponse);
    }

}
