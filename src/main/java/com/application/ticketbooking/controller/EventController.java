package com.application.ticketbooking.controller;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.service.EventService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для управления мероприятиями.
 * Обрабатывает запросы на создание, обновление, удаление и получение мероприятий.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Мероприятие найдено."),
        @ApiResponse(responseCode = "400", description = "Мероприятие не найдено.")
})
public class EventController {

    private final EventService eventService;

    /**
     * Получает список всех мероприятий с пагинацией и сортировкой.
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 20)
     * @param sortBy поле для сортировки (по умолчанию "id")
     * @return {@link ResponseEntity} с объектом Map, содержащей список мероприятий и информацию о пагинации
     */
    @GetMapping
    @Cacheable("events")
    public ResponseEntity<Map<String, Object>> getAllEvents(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortBy) {

        Page<Event> eventsPage = eventService.getAllEvents(page, size, sortBy);

        Map<String, Object> response = new HashMap<>();
        response.put("events", eventsPage.getContent());
        response.put("page", eventsPage.getNumber());
        response.put("size", eventsPage.getSize());
        response.put("totalElements", eventsPage.getTotalElements());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Получает мероприятие по его идентификатору.
     *
     * @param id идентификатор мероприятия {@link Event}
     * @return {@link ResponseEntity} с {@link Event}
     */
    @ApiResponse(responseCode = "200", description = "Мероприятие найдено")
    @ApiResponse(responseCode = "404", description = "Мероприятие не найдено.")
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable("id") Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok().body(event);
    }

    /**
     * Создает новое мероприятие.
     *
     * @param event объект {@link Event}, содержащий данные нового мероприятия
     * @return {@link ResponseEntity} с {@link EventResponse}, содержащим информацию о созданном мероприятии
     */
    @PostMapping()
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<EventResponse> createEvent(@RequestBody Event event) {
        EventResponse eventResponse = eventService.createEvent(event);
        eventResponse.setMessage("Мероприятие успешно создано.");

        return ResponseEntity.ok().body(eventResponse);
    }

    /**
     * Обновляет информацию о мероприятии.
     *
     * @param id идентификатор мероприятия
     * @param event объект {@link Event} с обновленными данными
     * @return {@link ResponseEntity} с {@link EventResponse}, содержащим информацию об обновленном мероприятии
     */
    @PutMapping("/{id}")
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<EventResponse> updateEvent(@PathVariable("id") Long id, @RequestBody Event event) {
        EventResponse eventResponse = eventService.updateEvent(event, id);
        eventResponse.setMessage("Мероприятие успешно обновлено.");
        return ResponseEntity.ok().body(eventResponse);
    }

    /**
     * Удаляет мероприятие по идентификатору.
     *
     * @param id идентификатор мероприятия {@link Event}
     * @return {@link ResponseEntity} с сообщением об успешном удалении или статусом 204, если мероприятие не найдено
     */
    @DeleteMapping("/{id}")
    @CacheEvict(value = "events", allEntries = true)
    public ResponseEntity<Map<String,Object>> deleteEvent(@PathVariable("id") Long id) {
        boolean isDeleted = eventService.deleteEvent(id);

        if (isDeleted) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Мероприятие успешно удалено.");
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Обрабатывает ошибки, связанные с выполнением запросов.
     *
     * @param exception {@link RuntimeException}.
     * @return {@link ResponseEntity} с сообщением об ошибке и HttpStatus.NOT_FOUND.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException exception) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("exception", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
