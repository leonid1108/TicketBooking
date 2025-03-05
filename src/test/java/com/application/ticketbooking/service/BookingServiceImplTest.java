package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.exception.BadRequestException;
import com.application.ticketbooking.exception.EntityNotFoundException;
import com.application.ticketbooking.repository.BookingRepository;
import com.application.ticketbooking.repository.EventRepository;
import com.application.ticketbooking.service.Impl.BookingServiceImpl;
import com.application.ticketbooking.service.Impl.NotificationLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Тестирование работы класса сервиса BookingServiceImpl")
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private NotificationLogServiceImpl notificationLogService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequest bookingRequest;
    private Booking booking;
    private Event event;
    private BookingResponse bookingResponse;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        event = new Event();
        event.setId(1L);
        event.setAvailableSeats(10);

        bookingRequest = new BookingRequest();
        bookingRequest.setEventId(1L);
        bookingRequest.setTicketsCount(2);

        booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTicketsCount(2);

        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setUserId(1L);
        bookingResponse.setEventId(1L);
        bookingResponse.setBookingDate(LocalDateTime.now());
        bookingResponse.setTicketsCount(2);
        bookingResponse.setMessage("Бронирование успешно выполнено.");
    }

    @Test
    @DisplayName("Успешное выполнение бронирования мест на мероприятие")
    void testBookingTickets_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.saveAndFlush(any(Booking.class))).thenReturn(booking);
        when(modelMapper.map(any(Booking.class), eq(BookingResponse.class))).thenReturn(bookingResponse);

        BookingResponse result = bookingService.bookingTickets(bookingRequest);

        assertNotNull(result);
        assertEquals("Бронирование успешно выполнено.", result.getMessage());
        verify(eventRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).saveAndFlush(any(Booking.class));
        verify(notificationLogService, times(1)).setNotificationLog(bookingResponse);
    }

    @Test
    @DisplayName("Ошибка бронирования из-за недостатка мест")
    void testBookingTickets_InsufficientSeats() {
        event.setAvailableSeats(1);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            bookingService.bookingTickets(bookingRequest);
        });

        assertEquals("Недостаточно мест на мероприятии", exception.getMessage());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка из-за бронирования мест на несуществующее мероприятие")
    void testBookingTickets_EventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.bookingTickets(bookingRequest);
        });

        assertEquals("Мероприятие не найдено.", exception.getMessage());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Успешное выполнение получения всех бронирований")
    void testGetAllBookings() {
        Page<Booking> bookingsPage = mock(Page.class);
        when(bookingRepository.findAll(PageRequest.of(0, 10))).thenReturn(bookingsPage);

        Page<Booking> result = bookingService.getAllBookings(0, 10);

        assertNotNull(result);
        verify(bookingRepository, times(1)).findAll(PageRequest.of(0, 10));
    }
}
