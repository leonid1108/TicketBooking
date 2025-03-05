package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.exception.EntityNotFoundException;
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

/**
 * Реализация сервиса для управления мероприятиями.
 * <p>
 * Обеспечивает CRUD операции для {@link Event}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    /**
     * Получает мероприятие по его идентификатору.
     *
     * @param id идентификатор мероприятия
     * @return объект {@link Event}, найденный в базе данных
     * @throws EntityNotFoundException если мероприятие не найдено
     */
    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Мероприятие не найдено."));
    }

    /**
     * Получает список всех мероприятий с пагинацией и сортировкой.
     *
     * @param page номер страницы (начиная с 0)
     * @param size количество записей на странице
     * @param sort название поля, по которому производится сортировка (по возрастанию)
     * @return {@link Page<Event>} - страница мероприятий
     */
    @Override
    public Page<Event> getAllEvents(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
        return eventRepository.findAll(pageable);
    }

    /**
     * Создает новое мероприятие.
     *
     * @param event объект {@link Event}, содержащий информацию о мероприятии
     * @return {@link EventResponse} с данными созданного мероприятия
     */
    @Override
    public EventResponse createEvent(Event event) {
        event.setAvailableSeats(event.getCapacity());
        return modelMapper.map(eventRepository.save(event), EventResponse.class);
    }

    /**
     * Обновляет данные существующего мероприятия.
     *
     * @param event объект {@link Event} с обновленными данными
     * @param id идентификатор мероприятия, которое нужно обновить
     * @return {@link EventResponse} с обновленными данными
     * @throws EntityNotFoundException если мероприятие не найдено
     */
    @Override
    public EventResponse updateEvent(Event event, Long id) {
        Event updatedEvent = eventRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Мероприятие с id = " + id + " не найдено.")
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

        EventResponse eventResponse = modelMapper.map(eventRepository.save(updatedEvent), EventResponse.class);
        eventResponse.setMessage("Мероприятие успешно обновлено.");
        return eventResponse;
    }

    /**
     * Удаляет мероприятие по его идентификатору.
     *
     * @param id идентификатор мероприятия
     * @return {@code true}, если мероприятие было найдено и удалено, иначе {@code false}
     */
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
