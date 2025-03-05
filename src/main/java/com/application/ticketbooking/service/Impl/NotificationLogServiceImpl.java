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

/**
 * Реализация сервиса для работы с логами уведомлений.
 * <p>
 * Обеспечивает создание логов уведомлений, асинхронную обработку и сохранение
 * их в базу данных после завершения транзакции.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class NotificationLogServiceImpl implements NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private static final Logger log = LoggerFactory.getLogger(NotificationLogServiceImpl.class);
    private final ModelMapper modelMapper;

    /**
     * Создает и сохраняет лог уведомления после успешного выполнения транзакции.
     * Этот метод вызывается асинхронно после коммита транзакции в {@link BookingServiceImpl}.
     * <p>
     * Метод обрабатывает создание {@link NotificationLog} на основе ответа о бронировании
     * и сохраняет его в базу данных.
     * </p>
     *
     * @param bookingResponse объект {@link BookingResponse}, содержащий информацию о бронировании
     */
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

    /**
     * Получает список всех логов уведомлений с пагинацией.
     *
     * @param page номер страницы (начиная с 0)
     * @param size количество записей на странице
     * @return {@link Page<NotificationLog>} содержащий объекты логов уведомлений
     */
    @Override
    public Page<NotificationLog> getAllNotificationLogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationLogRepository.findAll(pageable);
    }

}
