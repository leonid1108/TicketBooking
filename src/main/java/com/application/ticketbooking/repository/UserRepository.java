package com.application.ticketbooking.repository;

import com.application.ticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Репозиторий для {@link User}
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return {@link Optional<User>} с найденным пользователем или пустой, если пользователь не найден
     */
    Optional<User> findByUsername(String username);
}