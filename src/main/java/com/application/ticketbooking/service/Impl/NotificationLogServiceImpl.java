package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.Booking;
import com.application.ticketbooking.entity.NotificationLog;
import com.application.ticketbooking.repository.NotificationLogRepository;
import com.application.ticketbooking.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationLogServiceImpl implements NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private static final Logger log = LoggerFactory.getLogger(NotificationLogServiceImpl.class);
    private final ModelMapper modelMapper;

    @Async
    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void setNotificationLog(BookingResponse bookingResponse) {
        try {
            Thread.sleep(2000);

            NotificationLog notificationLog = new NotificationLog();
            notificationLog.setBooking(modelMapper.map(bookingResponse, Booking.class));
            notificationLog.setNotificationMessage("Уведомление отправлено");
            notificationLog.setNotifiedAt(LocalDateTime.now());

            log.info("Логирование прошло успешно.");
            notificationLogRepository.save(notificationLog);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<NotificationLog> getAllNotificationLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationLogRepository.findAll(pageable);
    }

}
