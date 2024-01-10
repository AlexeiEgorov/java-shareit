package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserService {

    User save(User user);

    User patch(UserDto user, Long id);

    Collection<User> getAll();

    User get(Long id);

    void delete(Long id);

    Collection<BookerDto> findBookers(Set<Long> usersIds);
}
