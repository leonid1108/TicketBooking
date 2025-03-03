package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.repository.EventRepository;
import com.application.ticketbooking.service.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Мероприятие не найдено."));
    }

    @Override
    public Page<Event> getAllEvents(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return eventRepository.findAll(pageable);
    }

    @Override
    public EventResponse createEvent(Event event) {
        event.setAvailableSeats(event.getCapacity());
        return modelMapper.map(eventRepository.save(event), EventResponse.class);
    }

    @Override
    public EventResponse updateEvent(Event event, Long id) {
        Event updatedEvent = eventRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Мероприятие с id = " + id + " не найдено.")
        );
        updatedEvent.setName(event.getName());
        updatedEvent.setDescription(event.getDescription());
        updatedEvent.setEventDate(event.getEventDate());

        if (updatedEvent.getAvailableSeats() == updatedEvent.getCapacity()) {
            updatedEvent.setAvailableSeats(event.getCapacity());
        } else {
            updatedEvent.setAvailableSeats(event.getCapacity() - (updatedEvent.getCapacity() - updatedEvent.getAvailableSeats()));
        }
        updatedEvent.setCapacity(event.getCapacity());
        return modelMapper.map(eventRepository.save(updatedEvent), EventResponse.class);
    }

    @Override
    public boolean deleteEvent(Long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            eventRepository.delete(event.get());
            return true;
        }
        return false;
    }


}
