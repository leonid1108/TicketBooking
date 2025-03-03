package com.application.ticketbooking.controller;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.UserResponse;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки запросов, связанных с аутентификацией и регистрацией пользователей.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрирует нового пользователя.
     *
     * @param user объект пользователя, содержащий регистрационные данные.
     * @return {@link ResponseEntity} с {@link UserResponse}, содержащим информацию о зарегистрированном пользователе.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@RequestBody User user) {
        UserResponse userResponse = authService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
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

//    /**
//     * Обрабатывает ошибки, связанные с выполнением запросов.
//     *
//     * @param exception {@link RuntimeException}.
//     * @return {@link ResponseEntity} с сообщением об ошибке и HttpStatus.BAD_REQUEST.
//     */
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException exception) {
//        Map<String, String> errorResponse = new HashMap<>();
//        errorResponse.put("exception", exception.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
}
