package com.application.ticketbooking.controller;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
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
import java.time.LocalDateTime;
import java.util.Map;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Тестирование работы контроллера NotificationsLogController")
public class NotificationsLogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    private String jwtTokenUser;
    private String jwtTokenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        jdbcTemplate.execute("DELETE FROM ticket_booking.users");
        jdbcTemplate.execute("DELETE FROM ticket_booking.events");
        jdbcTemplate.execute("DELETE FROM ticket_booking.bookings");
        jdbcTemplate.execute("DELETE FROM ticket_booking.notifications_log");

        registerUser("testUser");
        registerAdmin("testAdmin");
        jwtTokenUser = getJwtToken("testUser", "password");
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

    private void registerUser(String username) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole("ROLE_USER");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    private void registerAdmin(String username) throws Exception {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setRole("ROLE_ADMIN");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    private Long createEvent() throws Exception {
        Event event = new Event();
        event.setName("Test Event");
        event.setDescription("Event X");
        event.setEventDate(LocalDateTime.now().plusDays(5));
        event.setCapacity(100);
        event.setAvailableSeats(100);

        String response = mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
        return Long.valueOf(responseMap.get("id").toString());
    }

    private Long bookTickets(Long eventId, int ticketsCount) throws Exception {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setEventId(eventId);
        bookingRequest.setTicketsCount(ticketsCount);

        String response = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(response, new TypeReference<>() {});
        return Long.valueOf(responseMap.get("id").toString());
    }

    @Test
    @DisplayName("Получение логов уведомлений")
    void getAllNotifications_Success() throws Exception {
        Long eventId = createEvent();
        Long bookingId = bookTickets(eventId, 2);

        SECONDS.sleep(3);

        mockMvc.perform(get("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications").isArray())
                .andExpect(jsonPath("$.notifications[0].bookingId").value(bookingId.intValue()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Получение логов уведомлений с пагинацией")
    void getAllNotifications_Pagination_Success() throws Exception {
        Long eventId = createEvent();
        Long bookingId1 = bookTickets(eventId, 2);
        Long bookingId2 = bookTickets(eventId, 3);

        SECONDS.sleep(3);

        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM ticket_booking.notifications_log", Integer.class);
        assertEquals(2, count);

        mockMvc.perform(get("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifications").isArray())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}