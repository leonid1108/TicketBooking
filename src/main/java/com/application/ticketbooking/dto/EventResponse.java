package com.application.ticketbooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO для ответа на запрос о событии.
 * Содержит информацию о событии, включая его название, описание, дату, вместимость,
 * доступные места и сообщение о статусе события.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private int capacity;
    private int availableSeats;
    private String message;
}
