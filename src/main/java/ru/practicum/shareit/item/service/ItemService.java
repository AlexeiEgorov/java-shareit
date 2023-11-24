package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {
    Item save(Item item, Long ownerId);

    Item patch(ItemDto patch, Long ownerId, Long id);

    Collection<Item> getUserItems(Long ownerId);

    Item get(Long id);

    void delete(Long ownerId, Long id);

    Collection<Item> findByText(Long userId, String text);

    User getUser(Long userId);

    Collection<BookingItemDto> findBookingItemsByOwner(Long ownerId);

    CommentDto addComment(Long userId, Long itemId, CommentTextDto textDto);

    Map<Long, List<Comment>> getCommentsByItemsIds(Set<Long> itemsIds);
}
