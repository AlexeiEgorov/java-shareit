package ru.practicum.shareit.request.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequestResponseDto toDto(ItemRequest itemRequest) {
        return new ItemRequestResponseDto(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }
}
