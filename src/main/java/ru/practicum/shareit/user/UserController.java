package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public UserCreationDto add(@Valid @RequestBody UserCreationDto user) {
        return userServiceImpl.add(user);
    }

    @PatchMapping("/{id}")
    public ResponseUserDto update(@Valid @RequestBody UserPatchDto patch, @PathVariable long id) {
        return userServiceImpl.update(patch, id);
    }

    @GetMapping
    public Collection<ResponseUserDto> getAll() {
        return userServiceImpl.getAll();
    }

    @GetMapping("/{id}")
    public ResponseUserDto get(@PathVariable long id) {
        return userServiceImpl.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userServiceImpl.delete(id);
    }
}
