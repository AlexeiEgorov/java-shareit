package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.model.Marker;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    @Validated(Marker.Create.class)
    public UserDto add(@Valid @RequestBody UserDto user) {
        user.setId(userService.add(user));
        return user;
    }

    @PatchMapping("/{id}")
    @Validated(Marker.Update.class)
    public UserDto update(@Valid @RequestBody UserDto patch, @PathVariable Long id) {
        return userService.update(patch, id);
    }

    @GetMapping
    public Collection<ResponseUserDto> getAll() {
        return userService.getAll();
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
