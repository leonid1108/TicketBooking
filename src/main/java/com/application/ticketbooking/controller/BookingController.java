package com.application.ticketbooking.controller;

import com.application.ticketbooking.controller.api.BookingApi;
import com.application.ticketbooking.dto.BookingPageResponse;
import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер для управления бронированием билетов.
 * Обрабатывает запросы на бронирование, получение списка бронирований и обработку ошибок.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController implements BookingApi {

    private final BookingService bookingService;

    /**
     * Оформляет бронирование билетов на мероприятие.
     *
     * @param bookingRequest запрос на бронирование с информацией о мероприятии и количестве билетов.
     * @return {@link ResponseEntity} с {@link BookingResponse}, содержащим данные о бронировании.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> bookTickets(@RequestBody BookingRequest bookingRequest) {
        BookingResponse bookingResponse = bookingService.bookingTickets(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    /**
     * Получает список всех бронирований с пагинацией.
     *
     * @param page номер страницы (по умолчанию 0).
     * @param size количество элементов на странице (по умолчанию 20).
     * @return {@link ResponseEntity} с объектом Map, содержащей список бронирований и мета-информацию о страницах.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooking(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<Booking> bookingsPage = bookingService.getAllBookings(page, size);

        List<BookingPageResponse> bookings = bookingsPage.getContent().stream()
                .map(booking -> new BookingPageResponse(
                        booking.getId(),
                        booking.getEvent().getId(),
                        booking.getBookingDate(),
                        booking.getTicketsCount()
                ))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("bookings", bookings);
        response.put("page", bookingsPage.getNumber());
        response.put("size", bookingsPage.getSize());
        response.put("totalElements", bookingsPage.getTotalElements());

        return ResponseEntity.ok().body(response);
    }

}
