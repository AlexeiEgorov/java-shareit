package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class BookingMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            .withZone(ZoneId.systemDefault());

    public static ResponseBookingDto toDto(Booking booking) {
        return new ResponseBookingDto(booking.getId(),
                formatter.format(booking.getStart()),
                formatter.format(booking.getEnd()),
                booking.getStatus());
    }
}
