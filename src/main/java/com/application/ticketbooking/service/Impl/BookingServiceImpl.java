package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.exception.BadRequestException;
import com.application.ticketbooking.repository.BookingRepository;
import com.application.ticketbooking.repository.EventRepository;
import com.application.ticketbooking.service.BookingService;
import com.application.ticketbooking.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import java.time.LocalDateTime;

/**
 * Реализация сервиса бронирования билетов.
 * <p>
 * Обеспечивает бронирование мест на мероприятия и получение списка бронирований.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final NotificationLogServiceImpl notificationLogService;

    /**
     * Осуществляет бронирование билетов на мероприятие.
     * <p>
     * Транзакция используется для обеспечения целостности данных при бронировании мест.
     * Уровень изоляции {@code Isolation.SERIALIZABLE} предотвращает конкурентные изменения данных,
     * обеспечивая корректное распределение мест между пользователями.
     * <p>
     * @param bookingRequest объект запроса на бронирование с информацией о мероприятии и количестве билетов
     * @return {@link BookingResponse} с данными о бронировании и подтверждающим сообщением
     * @throws EntityNotFoundException если мероприятие не найдено
     * @throws BadRequestException если недостаточно свободных мест
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponse bookingTickets(BookingRequest bookingRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(bookingRequest.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено."));

        if (event.getAvailableSeats() < bookingRequest.getTicketsCount()) {
            throw new BadRequestException("Недостаточно мест на мероприятии");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTicketsCount(bookingRequest.getTicketsCount());


        event.setAvailableSeats(event.getAvailableSeats() - bookingRequest.getTicketsCount());

        Booking savedBooking = bookingRepository.saveAndFlush(booking);
        eventRepository.save(event);

        BookingResponse bookingResponse = modelMapper.map(savedBooking, BookingResponse.class);
        bookingResponse.setMessage("Бронирование успешно выполнено.");

        notificationLogService.setNotificationLog(bookingResponse);

        return bookingResponse;
    }

    /**
     * Получает список всех бронирований с пагинацией.
     *
     * @param page номер страницы (начиная с 0)
     * @param size количество записей на странице
     * @return страничный список бронирований {@link Page}, содержащий объекты {@link Booking}
     */
    @Override
    public Page<Booking> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingRepository.findAll(pageable);
    }

}
