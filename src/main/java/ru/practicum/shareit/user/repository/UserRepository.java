package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Collection<BookerDto> findAllByIdIn(Set<Long> usersIds);
}
