package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.model.Marker;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    @Validated(Marker.Create.class)
    public ItemDto add(@Valid @RequestBody ItemDto item, @RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        item.setId(itemService.add(item, ownerId));
        return item;
    }

    @PatchMapping("/{id}")
    public ItemDto update(@Valid @RequestBody ItemDto patch,
                                  @RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        return itemService.update(patch, ownerId, id);
    }

    @GetMapping
    public Collection<ResponseItemDto> getUserItems(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseItemDto get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long id) {
        return mapper.toDto(itemService.get(userId, id));
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        itemService.delete(ownerId, id);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findByText(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                  @RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.findByText(userId, text);
    }

}
