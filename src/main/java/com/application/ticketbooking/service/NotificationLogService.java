package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.BookingResponse;
import com.application.ticketbooking.entity.NotificationLog;
import org.springframework.data.domain.Page;

/**
 * Сервис для работы с логами уведомлений.
 * <p>
 * Определяет методы для записи и получения логов уведомлений о бронировании.
 * </p>
 */
public interface NotificationLogService {

    /**
     * @param bookingResponse объект {@link BookingResponse}, содержащий данные о бронировании
     */
    void setNotificationLog(BookingResponse bookingResponse);

    /**
     * @param page номер страницы (начиная с 0)
     * @param size количество элементов на странице
     * @return {@link Page}<{@link NotificationLog}> страница логов уведомлений
     */
    Page<NotificationLog> getAllNotificationLogs(int page, int size);
}
