package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.Collection;

public interface ItemService {
    ItemCreationDto add(ItemCreationDto item, long ownerId);

    ResponseItemDto update(ItemPatchDto patch, long ownerId, long id);

    Collection<ResponseItemDto> getUserItems(long ownerId);

    ResponseItemDto get(long ownerId, long id);

    void delete(long ownerId, long id);

    Collection<ResponseItemDto> findByText(long userId, String text);
}
