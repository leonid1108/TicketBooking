package com.application.ticketbooking.service.Impl;

import com.application.ticketbooking.entity.User;
import com.application.ticketbooking.repository.UserRepository;
import com.application.ticketbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Пользователь не найден.")
                );
    }
}
