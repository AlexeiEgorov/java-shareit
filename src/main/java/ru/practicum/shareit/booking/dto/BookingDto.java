package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class BookingDto {
    private final Long id;
    @NotNull
    @FutureOrPresent
    private final LocalDateTime start;
    @NotNull
    @FutureOrPresent
    private final LocalDateTime end;
    private final Long itemId;
}