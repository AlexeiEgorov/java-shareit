package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.BookingItemDto;

@UtilityClass
public class BookingItemDtoMapper {
    public BookingItemDto toBookingItemDto(Item item) {
        return new BookingItemDto(item.getId(), item.getName());
    }
}
