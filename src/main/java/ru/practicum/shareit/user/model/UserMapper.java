package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static ResponseUserDto toDto(User user) {
        return new ResponseUserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
