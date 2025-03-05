package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.entity.NotificationLog;
import com.application.ticketbooking.service.Impl.NotificationLogServiceImpl;
import com.application.ticketbooking.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование работы класса сервиса NotificationLogServiceImpl")
@ExtendWith(MockitoExtension.class)
public class NotificationLogServiceImplTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationLogServiceImpl notificationLogService;

    private BookingResponse bookingResponse;
    private NotificationLog notificationLog;

    @BeforeEach
    void setUp() {
        bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setUserId(1L);
        bookingResponse.setEventId(1L);
        bookingResponse.setBookingDate(LocalDateTime.now());
        bookingResponse.setTicketsCount(2);
        bookingResponse.setMessage("Booking message");

        notificationLog = new NotificationLog();
        notificationLog.setBooking(new Booking());
        notificationLog.setNotificationMessage("Уведомление отправлено");
    }

    @Test
    @DisplayName("Успешное логирование")
    void testSetNotificationLog() {
        when(modelMapper.map(bookingResponse, Booking.class)).thenReturn(new Booking());

        notificationLogService.setNotificationLog(bookingResponse);

        verify(notificationLogRepository, times(1)).save(any(NotificationLog.class));
    }

    @Test
    @DisplayName("Успешное получение логов")
    void testGetAllNotificationLogs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<NotificationLog> notificationLogsPage = mock(Page.class);
        when(notificationLogRepository.findAll(pageable)).thenReturn(notificationLogsPage);

        Page<NotificationLog> result = notificationLogService.getAllNotificationLogs(0, 10);

        assertNotNull(result);
        verify(notificationLogRepository, times(1)).findAll(pageable);
    }
}

