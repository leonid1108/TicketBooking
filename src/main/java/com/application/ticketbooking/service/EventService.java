package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import org.springframework.data.domain.Page;

/**
 * Сервис для управления мероприятиями.
 * <p>
 * Определяет основные операции CRUD для работы с сущностью {@link Event}.
 * </p>
 */
public interface EventService {

    /**
     * @param id идентификатор мероприятия
     * @return {@link Event} найденное мероприятие
     */
    Event getEventById(Long id);

    /**
     * @param page номер страницы (начиная с 0)
     * @param size количество элементов на странице
     * @param sort поле, по которому выполняется сортировка
     * @return {@link Page}<{@link Event}> страница мероприятий
     */
    Page<Event> getAllEvents(int page, int size, String sort);

    /**
     * @param event объект {@link Event}, содержащий данные нового мероприятия
     * @return {@link EventResponse} ответ с данными созданного мероприятия
     */
    EventResponse createEvent(Event event);

    /**
     * @param event объект {@link Event} с обновленными данными
     * @param id идентификатор мероприятия, которое требуется обновить
     * @return {@link EventResponse} ответ с обновленными данными мероприятия
     */
    EventResponse updateEvent(Event event, Long id);

    /**
     * @param id идентификатор мероприятия
     * @return {@code true}, если мероприятие успешно удалено, иначе {@code false}
     */
    boolean deleteEvent(Long id);
}
