package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {
    public ResponseUserDto toDto(User user) {
        return new ResponseUserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
