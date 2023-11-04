package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

@Component
class ItemMapper {
    public ResponseItemDto toDto(Item item) {
        return new ResponseItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item toItem(ItemCreationDto itemCreationDto) {
        return new Item(itemCreationDto.getId(), itemCreationDto.getName(), itemCreationDto.getDescription(),
                itemCreationDto.getAvailable());
    }
}
