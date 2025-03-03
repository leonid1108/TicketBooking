package com.application.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа на запрос о регистрации с информацией о пользователе.
 * Содержит данные пользователя, такие как его идентификатор, имя пользователя, роль и сообщение.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private String message;
}
