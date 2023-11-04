package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;

public interface UserService {
    UserCreationDto add(UserCreationDto user);

    ResponseUserDto update(UserPatchDto patch, long id);

    Collection<ResponseUserDto> getAll();

    ResponseUserDto get(long id);

    void delete(long id);
}
