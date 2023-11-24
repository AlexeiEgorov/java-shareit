package ru.practicum.shareit.booking.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.InfoBookingDto;

@UtilityClass
public class BookingInfoMapper {
    public InfoBookingDto toInfoBookingDto(Booking booking) {
        return new InfoBookingDto(booking.getId(), booking.getBooker().getId());
    }
}
