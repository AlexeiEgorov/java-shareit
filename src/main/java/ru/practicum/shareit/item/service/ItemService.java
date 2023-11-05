package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Long add(ItemDto item, Long ownerId);

    ItemDto update(ItemDto patch, Long ownerId, Long id);

    Collection<ResponseItemDto> getUserItems(Long ownerId);

    Item get(Long ownerId, Long id);

    void delete(Long ownerId, Long id);

    Collection<ResponseItemDto> findByText(Long userId, String text);
}
