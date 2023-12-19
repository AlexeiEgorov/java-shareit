package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.BookerDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;
import static ru.practicum.shareit.booking.model.BookingMapper.*;
import static ru.practicum.shareit.item.model.BookingItemDtoMapper.*;
import static ru.practicum.shareit.user.model.BookerDtoMapper.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;

    @PostMapping
    public ResponseBookingDto add(@RequestBody @Valid BookingDto bookingDto,
                                  @RequestHeader(USER_ID_REQ_HEADER) Long userId) {
        return setupResponseBooking(service.add(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approve(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long bookingId,
                                    @RequestParam @NotNull Boolean approved) {
        return setupResponseBooking(service.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long bookingId) {
        return setupResponseBooking(service.getBooking(userId, bookingId));
    }

    @GetMapping
    public Collection<ResponseBookingDto> getUserBookings(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL")
                                                          State state) {
        BookerDto bookerDto = new BookerDto(userId);
        return service.getUserBookings(userId, state).stream().map(booking -> {
            final ResponseBookingDto newBooking = toDto(booking);
            newBooking.setBooker(bookerDto);
            newBooking.setItem(toBookingItemDto(booking.getItem()));
            return newBooking;
        }).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getUserOwnItemsBookings(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "ALL")
                                                          State state) {
        return service.getOwnerItemsBookings(userId, state).stream()
                .map(this::setupResponseBooking)
                .collect(Collectors.toList());
    }

    private ResponseBookingDto setupResponseBooking(Booking booking) {
        ResponseBookingDto resp = toDto(booking);
        resp.setItem(toBookingItemDto(booking.getItem()));
        resp.setBooker(toBookerDto(booking.getBooker()));
        return resp;
    }
}
