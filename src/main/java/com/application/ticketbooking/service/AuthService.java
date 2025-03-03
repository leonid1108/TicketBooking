package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.UserResponse;
import com.application.ticketbooking.entity.User;

/**
 * Сервис аутентификации и регистрации пользователей.
 */
public interface AuthService {

    /**
     * @param user объект {@link User}, содержащий данные нового пользователя
     * @return {@link UserResponse} с информацией о зарегистрированном пользователе
     */
    UserResponse registerUser(User user);

    /**
     * @param authRequest объект {@link AuthRequest}, содержащий учетные данные пользователя
     * @return {@link AuthResponse} с токеном доступа при успешной аутентификации
     */
    AuthResponse loginUser(AuthRequest authRequest);
}
