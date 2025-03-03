package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.BookingRepository;
import com.application.ticketbooking.repository.EventRepository;
import com.application.ticketbooking.service.BookingService;
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

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final NotificationLogServiceImpl notificationLogService;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponse bookingTickets(BookingRequest bookingRequest) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findById(bookingRequest.getEventId())
                .orElseThrow(() -> new RuntimeException("Мероприятие не найдено."));

        if (event.getAvailableSeats() < bookingRequest.getTicketsCount()) {
            throw new RuntimeException("Недостаточно мест на мероприятии");
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

    @Override
    public Page<Booking> getAllBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingRepository.findAll(pageable);
    }


}
