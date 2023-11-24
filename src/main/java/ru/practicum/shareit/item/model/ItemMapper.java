package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.ArrayList;

@UtilityClass
public class ItemMapper {
    public ResponseItemDto toDto(Item item) {
        return new ResponseItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), new ArrayList<>());
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), null, null);
    }
}
