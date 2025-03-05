package com.application.ticketbooking.service;

import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.UserRepository;
import com.application.ticketbooking.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование работы класса сервиса UserServiceImpl")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");
        mockUser.setRole("USER");
    }

    @Test
    @DisplayName("Успешный поиск пользователя по имени")
    void testGetUserByUsername_success() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));

        User user = userService.getUserByUsername(username);

        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Ошибка поиска пользователя по имени")
    void testGetUserByUsername_userNotFound() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}
