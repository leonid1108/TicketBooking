package com.application.ticketbooking.controller;

import com.application.ticketbooking.dto.AuthRequest;
import com.application.ticketbooking.dto.BookingRequest;
import com.application.ticketbooking.entity.Event;
import com.application.ticketbooking.entity.User;
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
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Тестирование работы контроллера BookingController")
public class BookingControllerIntegrationTest {

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
        String sql = "DELETE FROM ticket_booking.users WHERE username = ?";
        jdbcTemplate.update(sql, username);

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

    private Long createEvent() throws Exception {
        jdbcTemplate.execute("DELETE FROM ticket_booking.events");

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

    @Test
    @DisplayName("Успешное бронирование билетов")
    void bookTickets_Success() throws Exception {
        Long eventId = createEvent();

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setEventId(eventId);
        bookingRequest.setTicketsCount(2);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.ticketsCount").value(2));
    }

    @Test
    @DisplayName("Получение списка бронирований")
    void getAllBooking_Success() throws Exception {
        Long eventId = createEvent();

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setEventId(eventId);
        bookingRequest.setTicketsCount(3);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtTokenUser)
                .content(objectMapper.writeValueAsString(bookingRequest)));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.bookings[0].eventId").value(eventId))
                .andExpect(jsonPath("$.bookings[0].ticketsCount").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Получение списка бронирований с пагинацией")
    void getAllBooking_Pagination_Success() throws Exception {
        Long eventId = createEvent();

        BookingRequest bookingRequest1 = new BookingRequest();
        bookingRequest1.setEventId(eventId);
        bookingRequest1.setTicketsCount(4);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtTokenUser)
                .content(objectMapper.writeValueAsString(bookingRequest1)));

        BookingRequest bookingRequest2 = new BookingRequest();
        bookingRequest2.setEventId(eventId);
        bookingRequest2.setTicketsCount(5);

        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtTokenUser)
                .content(objectMapper.writeValueAsString(bookingRequest2)));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookings").isArray())
                .andExpect(jsonPath("$.bookings[0].eventId").value(eventId))
                .andExpect(jsonPath("$.bookings[0].ticketsCount").value(5))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Успешное бронирование билетов и проверка базы данных")
    void bookTickets_Success_And_VerifyDatabase() throws Exception {
        Long eventId = createEvent();

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setEventId(eventId);
        bookingRequest.setTicketsCount(2);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated());

        Integer availableSeats = jdbcTemplate.queryForObject(
                "SELECT available_seats FROM ticket_booking.events WHERE id = ?", Integer.class, eventId);
        assertEquals(98, availableSeats);
    }
}
