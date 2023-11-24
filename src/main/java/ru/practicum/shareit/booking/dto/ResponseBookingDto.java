package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.user.dto.BookerDto;

import java.time.LocalDateTime;

import static ru.practicum.shareit.Constants.TIME_FORMAT;

@Setter
@Getter
@RequiredArgsConstructor
public class ResponseBookingDto {
    private final Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private final LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_FORMAT)
    private final LocalDateTime end;
    private BookingItemDto item;
    private BookerDto booker;
    private final Status status;
}
