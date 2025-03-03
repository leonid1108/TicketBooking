package com.application.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса на создание бронирования.
 * Содержит информацию о событии и количестве билетов, которые пользователь хочет забронировать.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long eventId;
    private int ticketsCount;
}
