package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;
import static ru.practicum.shareit.item.model.CommentMapper.*;
import static ru.practicum.shareit.item.model.ItemMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;
    private final BookingService bookingService;

    @PostMapping
    public ResponseItemDto add(@RequestBody ItemDto item, @RequestHeader(USER_ID_REQ_HEADER) Long ownerId) {
        return toDto(service.save(item, ownerId));
    }

    @PatchMapping("/{id}")
    public ResponseItemDto update(@RequestBody ItemDto patch, @RequestHeader(USER_ID_REQ_HEADER) Long ownerId,
                                  @PathVariable Long id) {
        return toDto(service.patch(patch, ownerId, id));
    }

    @GetMapping
    public Collection<ResponseItemDto> getUserItems(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        List<ResponseItemDto> items = service.getUserItems(ownerId, from, size)
                .stream()
                .map(ItemMapper::toDto)
                .sorted(Comparator.comparing(i -> {
                    if (i.getNextBooking() == null) {
                        return 1L;
                    }
                    return i.getNextBooking().getId() * -1;
                })
                ).collect(Collectors.toList());
        Set<Long> itemsIds = items
                .stream()
                .map(ResponseItemDto::getId)
                .collect(Collectors.toSet());
        loadWithLastAndNextBookings(items, itemsIds);
        loadWithComments(items, itemsIds);

        return items;
    }

    @GetMapping("/{id}")
    public ResponseItemDto get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long id) {
        Item item = service.get(id);
        List<ResponseItemDto> itemDto = List.of(toDto(item));
        Set<Long> itemId = Set.of(item.getId());

        if (item.getOwner().getId().equals(userId)) {
            loadWithLastAndNextBookings(itemDto, itemId);
        }
        loadWithComments(itemDto, itemId);
        return itemDto.get(0);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId, @PathVariable Long id) {
        service.delete(ownerId, id);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findByText(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                  @RequestParam String text,
                                                  @RequestParam Integer from,
                                                  @RequestParam Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return service.findByText(userId, text, from, size).stream().map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long itemId,
                                 @RequestBody CommentTextDto textDto) {
        return service.addComment(userId, itemId, textDto);
    }

    private void loadWithLastAndNextBookings(List<ResponseItemDto> items, Set<Long> itemsIds) {
        Map<Long, List<Booking>> itemsBookings = bookingService.findLastAndNextBookings(itemsIds);
        final LocalDateTime now = LocalDateTime.now();
        for (ResponseItemDto item : items) {
            Optional<Booking> last = itemsBookings.getOrDefault(item.getId(), List.of()).stream()
                    .filter(b -> !b.getStart().isAfter(now))
                    .reduce((first, second) -> second);
            if (last.isPresent()) {
                item.setLastBooking(BookingMapper.toInfoBookingDto(last.get()));
                Optional<Booking> next = itemsBookings.get(item.getId()).stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .findFirst();
                next.ifPresent(b -> item.setNextBooking(BookingMapper.toInfoBookingDto(b)));
            }
        }
    }

    private void loadWithComments(List<ResponseItemDto> items, Set<Long> itemsIds) {
        Map<Long, List<Comment>> itemsComments = service.getCommentsByItemsIds(itemsIds);
        Map<Long, String> authorsNames = new HashMap<>();
        for (ResponseItemDto item : items) {
            for (Comment comment : itemsComments.getOrDefault(item.getId(), List.of())) {
                final CommentDto commentDto = toCommentDto(comment);
                commentDto.setAuthorName(authorsNames.computeIfAbsent(
                        comment.getAuthor().getId(), k -> comment.getAuthor().getName()));
                item.getComments().add(commentDto);
            }
        }
    }
}
