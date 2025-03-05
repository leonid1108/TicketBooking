package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.UserRepository;
import com.application.ticketbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация сервиса для работы с пользователями.
 * <p>
 * Обеспечивает операции поиска пользователя по имени пользователя.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Получает пользователя по его имени пользователя.
     *
     * @param username имя пользователя
     * @return объект {@link User}, найденный в базе данных
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Пользователь не найден.")
                );
    }
}
