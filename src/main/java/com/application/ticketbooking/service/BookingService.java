package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import org.springframework.data.domain.Page;

/**
 * Сервис для управления бронированием билетов.
 */
public interface BookingService {

    /**
     * @param bookingRequest объект {@link BookingRequest}, содержащий данные о количестве билетов и id мероприятия
     * @return {@link BookingResponse} с информацией о бронировании
     */
    BookingResponse bookingTickets(BookingRequest bookingRequest);

    /**
     * @param page номер страницы (начиная с 0)
     * @param size количество элементов на странице
     * @return {@link Page}<{@link Booking}> страница бронирований с указанными параметрами
     */
    Page<Booking> getAllBookings(int page, int size);
}
