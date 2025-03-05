package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация {@link UserDetailsService} для загрузки информации о пользователе
 * при аутентификации.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserServiceImpl userService;

    /**
     * Загружает пользователя по его имени.
     * <p>
     * Метод получает данные пользователя из {@link UserServiceImpl} и
     * оборачивает их в {@link CustomUserDetails} для использования в Spring Security.
     * </p>
     *
     * @param username имя пользователя
     * @return объект {@link UserDetails}, содержащий информацию о пользователе
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        return new CustomUserDetails(user);
    }
}
