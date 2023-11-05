package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Map<Long, Item>> usersItems;
    private final Map<Long, Item> items;
    private Long nextItemId = 1L;

    @Override
    public Long add(Item item, User owner) {
        item.setId(nextItemId);
        usersItems.get(item.getOwner().getId()).put(nextItemId, item);
        items.put(nextItemId, item);
        return nextItemId++;
    }

    @Override
    public Collection<Item> getUserItems(Long ownerId) {
        return usersItems.get(ownerId).values();
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void delete(Long ownerId, Long id) {
        items.remove(id);
        usersItems.get(ownerId).remove(id);
    }

    @Override
    public Collection<Item> findByText(String text) {
        String lText = text.toLowerCase();

        return items.values().stream().filter(item -> (item.getName().toLowerCase().contains(lText)
                || item.getDescription().toLowerCase().contains(lText))
                && item.getAvailable()).collect(Collectors.toList());
    }

    @Override
    public void addUserItems(Long ownerId) {
        usersItems.put(ownerId, new HashMap<>());
    }

    @Override
    public void deleteAllUserItems(Long ownerId) {
        for (Item item : usersItems.get(ownerId).values()) {
            items.remove(item.getId());
        }
        usersItems.remove(ownerId);
    }

    @Override
    public boolean checkUserHasItem(Long ownerId, Long id) {
        return usersItems.get(ownerId).containsKey(id);
    }
}
