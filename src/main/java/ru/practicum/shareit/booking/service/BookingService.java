package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingService {
    Booking add(BookingDto bookingDto, Long userId);

    Booking approve(Long userId, Long bookingId, Boolean approved);

    Booking getBooking(Long userId, Long id);

    Collection<Booking> getUserBookings(Long userId, State state);

    Collection<Booking> getOwnerItemsBookings(Long userId, State state);

    Item getItem(Long itemId);

    User getUser(Long userId);

    Booking get(Long id);

    Map<Long, List<Booking>> findLastAndNextBookings(Set<Long> itemsIds);
}
