package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.model.Marker;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserDto add(@RequestBody @Validated(Marker.Create.class) UserDto user) {
        user.setId(userService.add(mapper.toUser(user)));
        return user;
    }

    @PatchMapping("/{id}")
    public ResponseUserDto update(@RequestBody @Validated(Marker.Update.class) UserDto patch, @PathVariable Long id) {
        return mapper.toDto(userService.update(patch, id));
    }

    @GetMapping
    public Collection<ResponseUserDto> getAll() {
        return userService.getAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseUserDto get(@PathVariable Long id) {
        return mapper.toDto(userService.get(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
