package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.user.dto.BookerDto;

@Setter
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class ResponseBookingDto {
    private final Long id;
    private final String start;
    private final String end;
    private BookingItemDto item;
    private BookerDto booker;
    private final Status status;
}
