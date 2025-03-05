package com.application.ticketbooking.controller.api;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.AuthResponse;
import com.application.ticketbooking.dto.RegisterResponse;
import com.application.ticketbooking.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Интерфейс, определяющий API для аутентификации и регистрации пользователей.
 * Содержит Swagger-аннотации для автоматической генерации документации.
 */
public interface AuthApi {

    @Operation(summary = "Регистрация нового пользователя", description = "Регистрирует нового пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос или пользователь с таким именем уже существует", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/signup")
    ResponseEntity<RegisterResponse> registerUser(
            @Parameter(description = "Объект пользователя с регистрационными данными.", required = true)
            @RequestBody User user
    );

    @Operation(summary = "Вход пользователя в систему", description = "Выполняет вход пользователя в систему.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вход успешно выполнен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Неверный логин или пароль", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> loginUser(
            @Parameter(description = "Объект AuthRequest с учетными данными пользователя.", required = true)
            @RequestBody AuthRequest authRequest
    );
}