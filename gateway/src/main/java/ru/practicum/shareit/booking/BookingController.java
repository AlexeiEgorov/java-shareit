package ru.practicum.shareit.booking;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid BookingDto bookingDto,
                                  @RequestHeader(USER_ID_REQ_HEADER) Long userId) {
        return client.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return client.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        return client.get(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                              @RequestParam(defaultValue = "ALL")
                                                                  String state,
                                                              @RequestParam(defaultValue = "0")
                                                                  @PositiveOrZero Integer from,
                                                              @RequestParam(defaultValue = "10")
                                                                  @Positive Integer size) {
        State stateParam = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return client.getUserBookings(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUserOwnItemsBookings(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                                  @RequestParam(defaultValue = "ALL")
                                                                      String state,
                                                                  @RequestParam(defaultValue = "0")
                                                                      @PositiveOrZero Integer from,
                                                                  @RequestParam(defaultValue = "10")
                                                                      @Positive Integer size) {
        State stateParam = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return client.getOwnerItemsBookings(userId, stateParam, from, size);
    }
}
