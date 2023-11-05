package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Long add(UserDto user);

    UserDto update(UserDto patch, Long id);

    Collection<ResponseUserDto> getAll();

    User get(Long id);

    void delete(Long id);
}
