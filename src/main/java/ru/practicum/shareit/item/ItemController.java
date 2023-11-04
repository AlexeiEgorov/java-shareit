package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    public ItemCreationDto add(@Valid @RequestBody ItemCreationDto item,
                               @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemServiceImpl.add(item, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseItemDto update(@Valid @RequestBody ItemPatchDto patch,
                                  @RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable @Positive long id) {
        return itemServiceImpl.update(patch, ownerId, id);
    }

    @GetMapping
    public Collection<ResponseItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemServiceImpl.getUserItems(ownerId);
    }

    @GetMapping("/{id}")
    public ResponseItemDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        return itemServiceImpl.get(userId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-Sharer-User-Id") long ownerId, @PathVariable long id) {
        itemServiceImpl.delete(ownerId, id);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam String text) {
        return itemServiceImpl.findByText(userId, text);
    }

}
