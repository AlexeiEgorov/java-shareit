package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class BookingDto {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;
}