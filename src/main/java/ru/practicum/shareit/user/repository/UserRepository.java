package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Collection<BookerDto> findAllByIdIn(Set<Long> usersIds);
}
