package com.application.ticketbooking.repository;

import com.application.ticketbooking.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для {@link NotificationLog}
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
}
