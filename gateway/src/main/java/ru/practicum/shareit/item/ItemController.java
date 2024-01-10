package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.model.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Validated(Marker.Create.class) ItemDto item,
                                      @RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        return client.add(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patch(@RequestBody @Validated(Marker.Update.class) ItemDto patch,
                          @RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long itemId) {
        return client.patch(patch, ownerId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        return client.getUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long itemId) {
        return client.getItem(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long itemId) {
        return client.delete(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                  @RequestParam String text,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive  Integer size) {
        return client.findByText(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long itemId,
                                 @RequestBody @Valid CommentTextDto textDto) {
        return client.addComment(userId, itemId, textDto);
    }
}
