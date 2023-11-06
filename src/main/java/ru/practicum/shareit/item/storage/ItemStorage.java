package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Long add(Item user, User owner);

    Collection<Item> getUserItems(Long ownerId);

    void delete(Long ownerId, Long id);

    Optional<Item> get(Long id);

    Collection<Item> findByText(String text);

    void deleteAllUserItems(Long ownerId);
}
