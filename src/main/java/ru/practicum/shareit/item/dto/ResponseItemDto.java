package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.InfoBookingDto;

import java.util.List;

@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ResponseItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private InfoBookingDto lastBooking;
    private InfoBookingDto nextBooking;
    private final List<CommentDto> comments;
    private final Long requestId;
}