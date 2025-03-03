package com.application.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO для ответа на запрос о создании бронирования.
 * Содержит информацию о бронировании, такую как идентификаторы пользователя и события,
 * дата бронирования, количество билетов и сообщение.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDateTime bookingDate;
    private int ticketsCount;
    private String message;
}
