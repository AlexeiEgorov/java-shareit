package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.model.Marker;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemDto add(@RequestBody @Validated(Marker.Create.class) ItemDto item,
                       @RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        item.setId(itemService.add(mapper.toItem(item), ownerId));
        return item;
    }

    @PatchMapping("/{id}")
    public ResponseItemDto update(@RequestBody ItemDto patch,
                          @RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        return mapper.toDto(itemService.update(patch, ownerId, id));
    }

    @GetMapping
    public Collection<ResponseItemDto> getUserItems(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        return itemService.getUserItems(ownerId).stream().map(mapper::toDto).collect(Collectors.toList());
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
        return itemService.findByText(userId, text).stream().map(mapper::toDto).collect(Collectors.toList());
    }

}
