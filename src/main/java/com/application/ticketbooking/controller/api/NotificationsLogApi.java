package com.application.ticketbooking.controller.api;

import com.application.ticketbooking.entity.NotificationLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

/**
 * Интерфейс, определяющий API для управления логами уведомлений.
 * Содержит Swagger-аннотации для автоматической генерации документации.
 */
@RequestMapping("/notifications")
public interface NotificationsLogApi {

    @Operation(summary = "Получение логов уведомлений", description = "Получает все логи уведомлений с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Логи уведомлений успешно получены",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationLog.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    ResponseEntity<Map<String, Object>> getAllNotifications(
            @Parameter(description = "Номер страницы (по умолчанию 0).", required = false)
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице (по умолчанию 20).", required = false)
            @RequestParam(value = "size", defaultValue = "20") int size
    );
}