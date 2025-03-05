package com.application.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO для ответа на запрос о получении всех бронирований.
 * Содержит информацию о мероприятии (id), дату бронирования и количество билетов.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPageResponse {
    private Long bookingId;
    private Long eventId;
    private LocalDateTime bookingDate;
    private int ticketsCount;
}
