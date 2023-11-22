package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

import static ru.practicum.shareit.Constants.ITEM;
import static ru.practicum.shareit.Constants.USER;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Long add(Item item, Long ownerId) {
        User owner = getUser(ownerId);
        item.setOwner(owner);
        return itemStorage.add(item, owner);
    }

    @Override
    public Item update(ItemDto patch, Long ownerId, Long id) {
        Item item = get(ownerId, id);
        if (!(item.getOwner().getId().equals(ownerId))) {
            throw new EntityNotFoundException(ITEM, id);
        }
        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        return item;
    }

    @Override
    public Collection<Item> getUserItems(Long ownerId) {
        getUser(ownerId);
        return itemStorage.getUserItems(ownerId);
    }

    @Override
    public Item get(Long userId, Long id) {
        getUser(userId);
        return itemStorage.get(id).orElseThrow(() -> new EntityNotFoundException(ITEM, id));
    }

    @Override
    public void delete(Long ownerId, Long id) {
        get(ownerId, id);
        itemStorage.delete(ownerId, id);
    }

    @Override
    public Collection<Item> findByText(Long userId, String text) {
        getUser(userId);
        return itemStorage.findByText(text);
    }

    @Override
    public User getUser(Long userId) {
        return userStorage.get(userId).orElseThrow(() -> new EntityNotFoundException(USER, userId));
    }
}
