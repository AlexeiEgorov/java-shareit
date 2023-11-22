package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;
import static ru.practicum.shareit.booking.model.BookingMapper.toDto;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ResponseBookingDto add(@RequestBody @Valid BookingDto bookingDto,
                                  @RequestHeader(USER_ID_REQ_HEADER) Long userId) {
        Booking booking = service.add(bookingDto, userId);
        ResponseBookingDto resp = toDto(booking);
        resp.setItem(new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
        resp.setBooker(new BookerDto(booking.getBooker().getId()));
        return resp;
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto approve(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long bookingId,
                                    @RequestParam @NotNull Boolean approved) {
        Booking booking = service.approve(userId, bookingId, approved);
        ResponseBookingDto resp = toDto(booking);
        resp.setItem(new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
        resp.setBooker(new BookerDto(booking.getBooker().getId()));
        return resp;
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getBooking(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long bookingId) {
        Booking booking = service.getBooking(userId, bookingId);
        ResponseBookingDto resp = toDto(booking);
        resp.setItem(new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
        resp.setBooker(new BookerDto(booking.getBooker().getId()));
        return resp;
    }

    @GetMapping
    public Collection<ResponseBookingDto> getUserBookings(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                          @RequestParam(required = false, defaultValue = "ALL")
                                                          String state) {
        BookerDto bookerDto = new BookerDto(userId);

        return service.getUserBookings(userId, state).stream().map(booking -> {
            final ResponseBookingDto newBooking = toDto(booking);
            newBooking.setBooker(bookerDto);
            newBooking.setItem(new BookingItemDto(booking.getItem().getId(), booking.getItem().getName()));
            return newBooking;
        }).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    @Transactional
    public Collection<ResponseBookingDto> getUserOwnItemsBookings(@RequestHeader(USER_ID_REQ_HEADER) Long ownerId,
                                                          @RequestParam(required = false, defaultValue = "ALL")
                                                          String state) {
        Map<Long, BookingItemDto> bookingItemsDtos = itemService.findBookingItemsByOwner(ownerId).stream()
                .collect(Collectors.toMap(BookingItemDto::getId, Function.identity()));
        Collection<Booking> bookings = service.getOwnerItemsBookings(state, bookingItemsDtos.keySet());

        Set<Long> bookersIds = bookings.stream()
                .map(booking -> booking.getBooker().getId())
                .collect(Collectors.toSet());
        Map<Long, BookerDto> bookersDtos = userService.findBookers(bookersIds).stream()
                .collect(Collectors.toMap(BookerDto::getId, Function.identity()));

        return bookings.stream().map(booking -> {
            final ResponseBookingDto newBooking = toDto(booking);
            newBooking.setBooker(bookersDtos.get(booking.getBooker().getId()));
            newBooking.setItem(bookingItemsDtos.get(booking.getItem().getId()));
            return newBooking;
        }).collect(Collectors.toList());
    }


}
