package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.BookingItemDto;

@UtilityClass
public class BookingItemDtoMapper {
    public BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(booking.getItem().getId(), booking.getItem().getName());
    }
}
