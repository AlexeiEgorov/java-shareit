package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return client.add(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID_REQ_HEADER) Long userId) {
        return client.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllNonOwnedRequests(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive  Integer size) {
        return client.getAllNonOwnedRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long requestId) {
        return client.get(userId, requestId);
    }
}