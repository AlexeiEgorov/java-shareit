package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private final long id;
    private final long ownerId;
    private final long recipientId;
    private final LocalDate from;
    private LocalDate to;
    private boolean confirmed;
    private final int rate;
    private final String commentary;
}
