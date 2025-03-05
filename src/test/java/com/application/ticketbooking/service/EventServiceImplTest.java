package com.application.ticketbooking.service;

import com.application.ticketbooking.dto.EventResponse;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.exception.EntityNotFoundException;
import com.application.ticketbooking.repository.EventRepository;
import com.application.ticketbooking.service.Impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование работы класса сервиса EventServiceImpl")
@ExtendWith(SpringExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private EventResponse eventResponse;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setDescription("Test Event Description");
        event.setCapacity(100);
        event.setAvailableSeats(100);

        eventResponse = new EventResponse();
        eventResponse.setId(1L);
        eventResponse.setName("Test Event");
        eventResponse.setDescription("Test Event Description");
        eventResponse.setCapacity(100);
        eventResponse.setAvailableSeats(100);
    }

    @Test
    @DisplayName("Успешное выполнение поиска мероприятия по id")
    void testGetEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка поиска мероприятия по id")
    void testGetEventById_NotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.getEventById(1L);
        });

        assertEquals("Мероприятие не найдено.", exception.getMessage());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Успешный поиск всех мероприятий")
    void testGetAllEvents() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());

        Event event1 = new Event();
        event1.setId(1L);
        event1.setName("Event 1");
        event1.setDescription("Event 1 Description");
        event1.setCapacity(100);
        event1.setAvailableSeats(80);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setName("Event 2");
        event2.setDescription("Event 2 Description");
        event2.setCapacity(200);
        event2.setAvailableSeats(150);

        Page<Event> page = new org.springframework.data.domain.PageImpl<>(Arrays.asList(event1, event2), pageable, 2);
        when(eventRepository.findAll(pageable)).thenReturn(page);

        Page<Event> result = eventService.getAllEvents(0, 10, "name");

        assertNotNull(result, "Результат не должен быть null");
        assertNotNull(result.getContent(), "Содержимое результата не должно быть null");
        assertEquals(2, result.getContent().size(), "Ожидаем 2 события");
        assertEquals("Event 1", result.getContent().get(0).getName(), "Имя первого события не совпадает");
        assertEquals("Event 2", result.getContent().get(1).getName(), "Имя второго события не совпадает");
        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Успешное создания мероприятия")
    void testCreateEvent_Success() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(modelMapper.map(any(Event.class), eq(EventResponse.class))).thenReturn(eventResponse);

        EventResponse result = eventService.createEvent(event);

        assertNotNull(result);
        assertEquals(eventResponse.getId(), result.getId());
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(modelMapper, times(1)).map(any(Event.class), eq(EventResponse.class));
    }

    @Test
    @DisplayName("Успешное обновления мероприятия")
    void testUpdateEvent_Success() {
        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setName("Updated Event");
        updatedEvent.setDescription("Updated Event Description");
        updatedEvent.setCapacity(120);
        updatedEvent.setAvailableSeats(100);

        EventResponse updatedEventResponse = new EventResponse();
        updatedEventResponse.setId(1L);
        updatedEventResponse.setName("Updated Event");
        updatedEventResponse.setDescription("Updated Event Description");
        updatedEventResponse.setCapacity(120);
        updatedEventResponse.setAvailableSeats(100);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(modelMapper.map(any(Event.class), eq(EventResponse.class))).thenReturn(updatedEventResponse);

        EventResponse result = eventService.updateEvent(updatedEvent, 1L);

        assertNotNull(result);
        assertEquals(updatedEvent.getName(), result.getName());
        assertEquals(updatedEvent.getCapacity(), result.getCapacity());
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(modelMapper, times(1)).map(any(Event.class), eq(EventResponse.class));
    }

    @Test
    @DisplayName("Ошибка обновления мероприятия")
    void testUpdateEvent_NotFound() {
        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setName("Updated Event");
        updatedEvent.setDescription("Updated Event Description");
        updatedEvent.setCapacity(120);
        updatedEvent.setAvailableSeats(100);

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.updateEvent(updatedEvent, 1L);
        });

        assertEquals("Мероприятие с id = 1 не найдено.", exception.getMessage());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Успешное удаление мероприятия")
    void testDeleteEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        boolean result = eventService.deleteEvent(1L);

        assertTrue(result);
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).delete(any(Event.class));
    }

    @Test
    @DisplayName("Ошибка удаления мероприятия")
    void testDeleteEvent_NotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = eventService.deleteEvent(1L);

        assertFalse(result);
        verify(eventRepository, times(1)).findById(1L);
    }
}
