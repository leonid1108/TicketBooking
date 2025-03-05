package com.application.ticketbooking.controller.api;

import com.application.ticketbooking.dto.BookingPageResponse;
import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

/**
 * Интерфейс, определяющий API для управления бронированием билетов.
 * Содержит Swagger-аннотации для автоматической генерации документации.
 */
public interface BookingApi {

    @Operation(summary = "Оформление бронирования билетов", description = "Оформляет бронирование билетов на мероприятие.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Бронирование успешно выполнено",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = @Content),
            @ApiResponse(responseCode = "404", description = "Ошибка бронирования из-за отсутствия мероприятия", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    ResponseEntity<BookingResponse> bookTickets(
            @Parameter(description = "Запрос на бронирование с информацией о мероприятии и количестве билетов.", required = true)
            @RequestBody BookingRequest bookingRequest
    );


    @Operation(summary = "Получение списка бронирований", description = "Получает список всех бронирований с пагинацией.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список бронирований успешно получен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingPageResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })

    @GetMapping
    ResponseEntity<Map<String, Object>> getAllBooking(
            @Parameter(description = "Номер страницы (по умолчанию 0).", required = false)
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Количество элементов на странице (по умолчанию 20).", required = false)
            @RequestParam(value = "size", defaultValue = "20") int size
    );
}