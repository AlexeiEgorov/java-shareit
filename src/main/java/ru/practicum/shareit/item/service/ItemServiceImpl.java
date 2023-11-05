package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.ITEM;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper mapper;

    @Override
    public Long add(ItemDto item, Long ownerId) {
        Item created = mapper.toItem(item);
        User owner = userService.get(ownerId);
        created.setOwner(owner);
        return itemStorage.add(created, owner);
    }

    @Override
    public ItemDto update(ItemDto patch, Long ownerId, Long id) {
        Item item = get(ownerId, id);
        if (!(item.getOwner().getId().equals(ownerId))) {
            throw new EntityNotFoundException(ITEM, id);
        }
        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        } else {
            patch.setName(item.getName());
        }
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
            item.setDescription(patch.getDescription());
        } else {
            patch.setDescription(item.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        } else {
            patch.setAvailable(item.getAvailable());
        }
        patch.setId(item.getId());
        return patch;
    }

    @Override
    public Collection<ResponseItemDto> getUserItems(Long ownerId) {
        userService.get(ownerId);
        return itemStorage.getUserItems(ownerId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Item get(Long userId, Long id) {
        userService.get(userId);
        return itemStorage.get(id).orElseThrow(() -> new EntityNotFoundException(ITEM, id));
    }

    @Override
    public void delete(Long ownerId, Long id) {
        get(ownerId, id);
        itemStorage.delete(ownerId, id);
    }

    @Override
    public Collection<ResponseItemDto> findByText(Long userId, String text) {
        userService.get(userId);
        return itemStorage.findByText(text).stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
