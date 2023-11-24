package ru.practicum.shareit.booking.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

@UtilityClass
public class BookingMapper {
    public ResponseBookingDto toDto(Booking booking) {
        return new ResponseBookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus());
    }
}
