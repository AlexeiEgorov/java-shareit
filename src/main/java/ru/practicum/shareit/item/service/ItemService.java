package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {

    @Transactional
    Item save(ItemDto item, Long ownerId);

    Item patch(ItemDto patch, Long ownerId, Long id);

    //Collection<Item> getUserItems(Long ownerId);

    Page<Item> getUserItems(Long ownerId, Integer from, Integer size);

    Item get(Long id);

    void delete(Long ownerId, Long id);

    //Collection<Item> findByText(Long userId, String text);

    Page<Item> findByText(Long userId, String text, Integer from, Integer size);

    User getUser(Long userId);

    ItemRequest getItemRequest(Long requestId);

    CommentDto addComment(Long userId, Long itemId, CommentTextDto textDto);

    Map<Long, List<Comment>> getCommentsByItemsIds(Set<Long> itemsIds);

    List<Item> findItemsByRequestsIds(Set<Long> requestsIds);
}
