package ru.practicum.shareit.user.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {
    public ResponseUserDto toDto(User user) {
        return new ResponseUserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public BookerDto toBookerDto(User user) {
        return new BookerDto(user.getId());
    }
}
