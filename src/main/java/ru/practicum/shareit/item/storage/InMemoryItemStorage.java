package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Map<Long, Item>> usersItems;
    private final Map<Long, Item> items;
    private long nextItemId = 1;
    private final ItemMapper mapper;

    @Override
    public ItemCreationDto add(ItemCreationDto item, long ownerId) {
        item.setId(nextItemId);
        Item created = mapper.toItem(item);
        usersItems.get(ownerId).put(nextItemId, created);
        items.put(nextItemId++, created);
        return item;
    }

    @Override
    public ResponseItemDto update(ItemPatchDto patch, long ownerId, long id) {
        Item item = usersItems.get(ownerId).get(id);
        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        return mapper.toDto(item);
    }

    @Override
    public Collection<ResponseItemDto> getUserItems(long ownerId) {
        return usersItems.get(ownerId).values().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ResponseItemDto find(long id) {
        return mapper.toDto(items.get(id));
    }

    @Override
    public void delete(long ownerId, long id) {
        items.remove(id);
        usersItems.get(ownerId).remove(id);
    }

    @Override
    public Collection<ResponseItemDto> findByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String lText = text.toLowerCase();
        List<ResponseItemDto> found = new ArrayList<>();

        for (Map<Long, Item> items : usersItems.values()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(lText)
                        || item.getDescription().toLowerCase().contains(lText))
                        && item.getAvailable()) {
                    found.add(mapper.toDto(item));
                }
            }
        }
        return found;
    }

    @Override
    public void addUserItems(long ownerId) {
        usersItems.put(ownerId, new HashMap<>());
    }

    @Override
    public boolean checkUserHasItem(long ownerId, long id) {
        return usersItems.get(ownerId).containsKey(id);
    }

    @Override
    public boolean containsItem(long id) {
        return items.containsKey(id);
    }

    @Override
    public void deleteAllUserItems(long ownerId) {
        for (Item item : usersItems.get(ownerId).values()) {
            items.remove(item.getId());
        }
        usersItems.remove(ownerId);
    }
}
