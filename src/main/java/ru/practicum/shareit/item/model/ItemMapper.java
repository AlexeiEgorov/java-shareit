package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ItemMapper {
    public ResponseItemDto toDto(Item item) {
        return new ResponseItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), null, null);
    }
}
