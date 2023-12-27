package ru.practicum.shareit.booking.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import static ru.practicum.shareit.Constants.FORMATTER;

@UtilityClass
public class BookingMapper {

    public ResponseBookingDto toDto(Booking booking) {
        return new ResponseBookingDto(booking.getId(),
                FORMATTER.format(booking.getStart()),
                FORMATTER.format(booking.getEnd()),
                booking.getStatus());
    }

    public InfoBookingDto toInfoBookingDto(Booking booking) {
        return new InfoBookingDto(booking.getId(), booking.getBooker().getId());
    }
}
