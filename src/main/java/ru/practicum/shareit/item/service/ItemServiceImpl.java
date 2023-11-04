package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.ValidationService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ValidationService validationService;
    private final ItemStorage itemStorage;

    @Override
    public ItemCreationDto add(ItemCreationDto item, long ownerId) {
        validationService.checkUserRegistration(ownerId);
        return itemStorage.add(item, ownerId);
    }

    @Override
    public ResponseItemDto update(ItemPatchDto patch, long ownerId, long id) {
        validationService.checkUserRegistration(ownerId);
        validationService.checkUserHasItem(ownerId, id);
        return itemStorage.update(patch, ownerId, id);
    }

    @Override
    public Collection<ResponseItemDto> getUserItems(long ownerId) {
        validationService.checkUserRegistration(ownerId);
        return itemStorage.getUserItems(ownerId);
    }

    @Override
    public ResponseItemDto get(long userId, long id) {
        validationService.checkUserRegistration(userId);
        validationService.checkItemRegistration(id);
        return itemStorage.find(id);
    }

    @Override
    public void delete(long ownerId, long id) {
        validationService.checkUserRegistration(ownerId);
        validationService.checkUserHasItem(ownerId, id);
        itemStorage.delete(ownerId, id);
    }

    @Override
    public Collection<ResponseItemDto> findByText(long userId, String text) {
        validationService.checkUserRegistration(userId);
        return itemStorage.findByText(text);
    }
}
