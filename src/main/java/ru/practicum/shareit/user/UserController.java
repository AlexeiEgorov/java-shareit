package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserCreationDto add(@Valid @RequestBody UserCreationDto user) {
        return userService.add(user);
    }

    @PatchMapping("/{id}")
    public ResponseUserDto update(@Valid @RequestBody UserPatchDto patch, @PathVariable long id) {
        return userService.update(patch, id);
    }

    @GetMapping
    public Collection<ResponseUserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseUserDto get(@PathVariable long id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
