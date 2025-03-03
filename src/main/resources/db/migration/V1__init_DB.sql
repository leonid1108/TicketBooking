CREATE SCHEMA IF NOT EXISTS ticket_booking;

CREATE TABLE ticket_booking.users (
      id SERIAL PRIMARY KEY,
      username VARCHAR(100) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL,
      role VARCHAR(50) NOT NULL,
      enabled BOOLEAN NOT NULL
);

CREATE TABLE ticket_booking.events (
       id SERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       description TEXT,
       event_date TIMESTAMP NOT NULL,
       capacity INTEGER,
       available_seats INTEGER
);

CREATE TABLE ticket_booking.bookings (
         id SERIAL PRIMARY KEY,
         user_id INTEGER NOT NULL,
         event_id INTEGER NOT NULL,
         booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         tickets_count INTEGER NOT NULL
);

CREATE TABLE ticket_booking.notifications_log (
          id SERIAL PRIMARY KEY,
          booking_id INTEGER NOT NULL,
          notification_message TEXT NOT NULL,
          notified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE IF EXISTS ticket_booking.bookings
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES ticket_booking.users(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS ticket_booking.bookings
    ADD CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES ticket_booking.events(id) ON DELETE CASCADE;

ALTER TABLE IF EXISTS ticket_booking.notifications_log
    ADD CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES ticket_booking.bookings(id) ON DELETE CASCADE;