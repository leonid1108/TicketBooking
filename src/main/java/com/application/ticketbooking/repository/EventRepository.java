package com.application.ticketbooking.repository;

import com.application.ticketbooking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для {@link Event}
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
