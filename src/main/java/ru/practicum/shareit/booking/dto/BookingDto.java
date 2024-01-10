package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.annotation.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@StartBeforeEndDateValid
public class BookingDto {
    private final Long id;
    @FutureOrPresent
    private final LocalDateTime start;
    private final LocalDateTime end;
    @NotNull
    private final Long itemId;
}