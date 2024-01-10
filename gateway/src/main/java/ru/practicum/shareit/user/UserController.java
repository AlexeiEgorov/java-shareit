package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.model.Marker;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated(Marker.Create.class) UserDto userDto) {
        return client.add(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patch(@RequestBody @Validated(Marker.Update.class) UserDto patch,
                                        @PathVariable Long userId) {
        return client.patch(patch, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        return client.get(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        return client.delete(userId);
    }
}
