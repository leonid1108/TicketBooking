package com.application.ticketbooking.controller;

import com.application.ticketbooking.controller.api.NotificationsLogApi;
import com.application.ticketbooking.entity.NotificationLog;
import com.application.ticketbooking.service.NotificationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления логами уведомлений.
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationsLogController implements NotificationsLogApi {

    private final NotificationLogService notificationLogService;

    /**
     * Получает все логи уведомлений с пагинацией.
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 20)
     * @return {@link ResponseEntity} с картой, содержащей список уведомлений и информацию о пагинации
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotifications(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<NotificationLog> notificationPage = notificationLogService.getAllNotificationLogs(page, size);

        List<Map<String, Object>> notifications = notificationPage.getContent().stream()
                .map(notification -> {
                    Map<String, Object> notificationMap = new LinkedHashMap<>();
                    notificationMap.put("id", notification.getId());
                    notificationMap.put("bookingId", notification.getBooking().getId());
                    notificationMap.put("notificationMessage", notification.getNotificationMessage());
                    notificationMap.put("notifiedAt", notification.getNotifiedAt());
                    return notificationMap;
                })
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("notifications", notifications);
        response.put("page", notificationPage.getNumber());
        response.put("size", notificationPage.getSize());
        response.put("totalElements", notificationPage.getTotalElements());

        return ResponseEntity.ok().body(response);
    }


}
