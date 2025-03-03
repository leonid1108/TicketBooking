package com.application.ticketbooking.service;

import com.application.ticketbooking.entity.User;

/**
 * Сервис для работы с пользователями.
 * <p>
 * Определяет методы для получения информации о пользователях.
 * </p>
 */
public interface UserService {

    /**
     * @param username имя пользователя
     * @return {@link User} найденный пользователь
     */
    User getUserByUsername(String username);
}
