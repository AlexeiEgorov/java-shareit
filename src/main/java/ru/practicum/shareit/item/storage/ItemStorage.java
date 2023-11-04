package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage {

    ItemCreationDto add(ItemCreationDto user, User owner);

    ResponseItemDto update(ItemPatchDto patch, long ownerId, long id);

    Collection<ResponseItemDto> getUserItems(long ownerId);

    void delete(long ownerId, long id);

    ResponseItemDto get(long id);

    Collection<ResponseItemDto> findByText(String text);

    void addUserItems(long ownerId);

    boolean checkUserHasItem(long ownerId, long id);

    boolean containsItem(long id);

    void deleteAllUserItems(long ownerId);
}
