package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

import java.util.Collection;

public interface UserStorage {

    UserCreationDto add(UserCreationDto user);

    ResponseUserDto update(UserPatchDto patch, long id);

    Collection<ResponseUserDto> getAll();

    void delete(long id);

    ResponseUserDto find(long id);

    boolean checkEmailExists(String email);

    String getUserEmail(long id);

    boolean containsUser(long id);
}
