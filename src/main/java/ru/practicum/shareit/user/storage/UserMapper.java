package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;

@Component
class UserMapper {
    public ResponseUserDto toDto(User user) {
        return new ResponseUserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserCreationDto userCreationDto) {
        return new User(userCreationDto.getId(), userCreationDto.getName(), userCreationDto.getEmail());
    }
}
