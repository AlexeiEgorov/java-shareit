package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.model.UserMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseUserDto add(@RequestBody UserDto user) {
        return toDto(userService.save(toUser(user)));
    }

    @PatchMapping("/{id}")
    public ResponseUserDto update(@RequestBody UserDto patch, @PathVariable Long id) {
        return toDto(userService.patch(patch, id));
    }

    @GetMapping
    public Collection<ResponseUserDto> getAll() {
        return userService.getAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseUserDto get(@PathVariable Long id) {
        return toDto(userService.get(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
