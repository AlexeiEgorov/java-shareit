package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemService {

    Long add(Item item, Long ownerId);

    Item update(ItemDto patch, Long ownerId, Long id);

    Collection<Item> getUserItems(Long ownerId);

    Item get(Long ownerId, Long id);

    void delete(Long ownerId, Long id);

    Collection<Item> findByText(Long userId, String text);

    User getUser(Long userId);
}
