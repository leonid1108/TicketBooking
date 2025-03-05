package com.application.ticketbooking.controller.api;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * Интерфейс, определяющий API для управления мероприятиями.
 * Содержит Swagger-аннотации для автоматической генерации документации.
 */
public interface EventApi {

    @Operation(summary = "Получение списка мероприятий", description = "Получает список всех мероприятий с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список мероприятий успешно получен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    ResponseEntity<Map<String, Object>> getAllEvents(
            @Parameter(description = "Номер страницы (по умолчанию 0).", required = false)
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "Количество элементов на странице (по умолчанию 20).", required = false)
            @RequestParam(value = "size", defaultValue = "20") int size,

            @Parameter(description = "Поле, по которому сортируется выборка (по умолчанию id).", required = false)
            @RequestParam(value = "sort", defaultValue = "id") String sortBy
    );

    @Operation(summary = "Получение мероприятия по id", description = "Получает мероприятие по переданному id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мероприятий успешно получено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<Event> getEventById(
            @Parameter(description = "id мероприятия", required = true)
            @PathVariable("id") Long id
    );

    @Operation(summary = "Создание мероприятия", description = "Создает мероприятие.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мероприятий успешно создано",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    ResponseEntity<EventResponse> createEvent(
            @Parameter(description = "Запрос на создание мероприятия с информацией о мероприятии.", required = true)
            @RequestBody Event event
    );

    @Operation(summary = "Обновление мероприятия", description = "Обновляет мероприятие.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мероприятий успешно обновлено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = @Content),
            @ApiResponse(responseCode = "404", description = "Мероприятие не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PutMapping
    ResponseEntity<EventResponse> updateEvent(
            @Parameter(description = "id мероприятия, которое будет обновлено", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Новые данные для обновления мероприятия", required = true)
            @RequestBody Event event
    );

    @Operation(summary = "Удаление мероприятия", description = "Обновляет мероприятие.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Мероприятий успешно обновлено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = boolean.class))}),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Map<String,Object>> deleteEvent(
            @Parameter(description = "id мероприятия, которое будет удалено", required = true)
            @PathVariable("id") Long id
    );
}

