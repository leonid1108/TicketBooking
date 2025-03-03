package com.application.ticketbooking.repository;

import com.application.ticketbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для {@link Booking}
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
