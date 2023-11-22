package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.model.Marker;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;
import static ru.practicum.shareit.item.model.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.model.ItemMapper.toDto;
import static ru.practicum.shareit.item.model.ItemMapper.toItem;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final BookingService bookingService;

    @PostMapping
    public ResponseItemDto add(@RequestBody @Validated(Marker.Create.class) ItemDto item,
                       @RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        return toDto(service.save(toItem(item), ownerId));
    }

    @PatchMapping("/{id}")
    public ResponseItemDto update(@RequestBody ItemDto patch,
                          @RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        return toDto(service.patch(patch, ownerId, id));
    }

    @GetMapping
    public Collection<ResponseItemDto> getUserItems(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        List<ResponseItemDto> items = service.getUserItems(ownerId).stream()
                .map(item -> {
                    ResponseItemDto newI = toDto(item);
                    loadWithLastAndNextBooking(newI);
                    return newI;
                })
                .sorted(Comparator.comparing(i -> {
                    if (i.getNextBooking() == null) {
                        return 1L;
                    }
                    return i.getNextBooking().getId() * -1;})
                ).collect(Collectors.toList());
        loadWithComments(items);

        return items;
    }

    @GetMapping("/{id}")
    public ResponseItemDto get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long id) {
        Item item = service.get(id);
        ResponseItemDto itemDto = toDto(item);
        if (item.getOwner().getId().equals(userId)) {
            loadWithLastAndNextBooking(itemDto);
        }
        loadWithComments(List.of(itemDto));
        return itemDto;
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        service.delete(ownerId, id);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findByText(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                  @RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return service.findByText(userId, text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long itemId,
                                 @RequestBody @Validated(Marker.Create.class) CommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }

    private void loadWithLastAndNextBooking(ResponseItemDto item) {
        Map<String, InfoBookingDto> lastAndNext = bookingService.findLastAndNextBookings(item.getId());
        item.setLastBooking(lastAndNext.get("last"));
        item.setNextBooking(lastAndNext.get("next"));
    }

    private void loadWithComments(List<ResponseItemDto> items) {
        List<List<Comment>> comments = items.stream().map(item -> service.getCommentsByItemId(item.getId()))
                .collect(Collectors.toList());
        Map<Long, String> authorsNames = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            for (Comment comment : comments.get(i)) {
                final CommentDto commentDto = toCommentDto(comment);
                commentDto.setAuthorName(authorsNames.computeIfAbsent(
                        comment.getAuthor().getId(), k -> comment.getAuthor().getName()));
                items.get(i).getComments().add(commentDto);
            }
        }
    }
}
