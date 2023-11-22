package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Long add(User user);

    User update(UserDto patch, Long id);

    Collection<User> getAll();

    User get(Long id);

    void delete(Long id);
}
