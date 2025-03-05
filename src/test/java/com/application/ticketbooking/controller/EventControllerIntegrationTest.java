package com.application.ticketbooking.controller;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Тестирование работы контроллера EventController")
public class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String jwtTokenAdmin;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }


    @BeforeEach
    void setUp() throws Exception {
        jdbcTemplate.execute("DELETE FROM ticket_booking.users");

        registerAdmin("testAdmin");
        jwtTokenAdmin = getJwtToken("testAdmin", "password");
    }

    private String getJwtToken(String username, String password) throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(username);
        authRequest.setPassword(password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, String> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
        return responseMap.get("token");
    }


    private void registerAdmin(String username) throws Exception {
        String sql = "DELETE FROM ticket_booking.users WHERE username = ?";
        jdbcTemplate.update(sql, username);

        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole("ROLE_ADMIN");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Получение списка мероприятий с пагинацией и сортировкой")
    void getAllEvents() throws Exception {
        mockMvc.perform(get("/events?page=0&size=10&sort=id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @DisplayName("Получение мероприятия по ID")
    void getEventById() throws Exception {
        Event event = new Event();
        event.setName("Test Event");
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now());
        event.setCapacity(100);
        event.setAvailableSeats(100);

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(get("/events/" + savedEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEvent.getId()))
                .andExpect(jsonPath("$.name").value("Test Event"));
    }

    @Test
    @DisplayName("Ошибка получения мероприятия по ID (не найдено)")
    void getEventById_NotFound() throws Exception {
        mockMvc.perform(get("/events/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Создание нового мероприятия")
    void createEvent() throws Exception {
        Event event = new Event();
        event.setName("New Event");
        event.setDescription("New Event Description");
        event.setEventDate(LocalDateTime.now());
        event.setCapacity(200);
        event.setAvailableSeats(200);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Мероприятие успешно создано."));
    }

    @Test
    @DisplayName("Обновление мероприятия")
    void updateEvent() throws Exception {
        Event event = new Event();
        event.setName("Old Event");
        event.setDescription("Old Description");
        event.setEventDate(LocalDateTime.now());
        event.setCapacity(150);
        event.setAvailableSeats(150);

        Event savedEvent = eventRepository.save(event);

        event.setName("Updated Event");
        event.setDescription("Updated Description");

        mockMvc.perform(put("/events/" + savedEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Мероприятие успешно обновлено."))
                .andExpect(jsonPath("$.name").value("Updated Event"));
    }

    @Test
    @DisplayName("Удаление мероприятия")
    void deleteEvent() throws Exception {
        Event event = new Event();
        event.setName("Event to be deleted");
        event.setDescription("Description");
        event.setEventDate(LocalDateTime.now());
        event.setCapacity(50);
        event.setAvailableSeats(50);

        Event savedEvent = eventRepository.save(event);

        mockMvc.perform(delete("/events/" + savedEvent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Мероприятие успешно удалено."));

        mockMvc.perform(get("/events/" + savedEvent.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Ошибка при удалении мероприятия (не найдено)")
    void deleteEvent_NotFound() throws Exception {
        mockMvc.perform(delete("/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isNoContent());
    }
}
