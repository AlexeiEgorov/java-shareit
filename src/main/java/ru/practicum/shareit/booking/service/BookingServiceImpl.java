package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConstraintViolationException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.Constants.*;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking add(BookingDto bookingDto, Long userId) {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ConstraintViolationException("Дата окончания должна быть после начала: ",
                    String.format("start: %s ; end: %s", bookingDto.getStart(), bookingDto.getEnd()));
        }
        Item item = getItem(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new ConstraintViolationException("Предмет в данный момент недоступен", item.getId().toString());
        } else if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(BOOKING, bookingDto.getItemId());
        }

        return repository.save(Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(getUser(userId))
                .status(Status.WAITING)
                .build());
    }

    @Override
    @Transactional
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = get(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(BOOKING, bookingId);
        } else if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ConstraintViolationException("Аренда уже подтверждена", bookingId.toString());
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        repository.changeStatus(booking.getStatus(), bookingId);

        return booking;
    }

    @Override
    public Booking getBooking(Long userId, Long id) {
        Booking booking = get(id);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new EntityNotFoundException(NO_ACCESS, id);
        }
        return booking;
    }

    @Override
    public Collection<Booking> getUserBookings(Long userId, String state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        switch (state.toLowerCase()) {
            case "all":
                return repository.findAllByBookerIdOrderByStartDesc(userId);
            case "current":
                return repository.findCurrentBookings(userId, now);
            case "future":
                return repository.findAllByBookerIdAndStatusInOrderByStartDesc(userId, Status.WAITING, Status.APPROVED);
            case "waiting":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
            case "rejected":
                return repository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
            case "past":
                return repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            default:
                throw new NotAllowedActionException("Unknown state: " + state, "");
        }
    }

    @Override
    public Collection<Booking> getOwnerItemsBookings(String state, Set<Long> itemsIds) {
        LocalDateTime now = LocalDateTime.now();

        switch (state.toLowerCase()) {
            case "all":
                return repository.findAllByItemIdInOrderByStartDesc(itemsIds);
            case "current":
                return repository.findCurrentOwnerBookings(itemsIds, now);
            case "future":
                return repository.findAllByItemIdInAndStatusInOrderByStartDesc(itemsIds, Status.APPROVED,
                        Status.WAITING);
            case "waiting":
                return repository.findAllByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.WAITING);
            case "rejected":
                return repository.findAllByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.REJECTED);
            case "past":
                return repository.findAllByItemIdInAndEndBeforeOrderByStartDesc(itemsIds, now);
            default:
                throw new NotAllowedActionException("Unknown state: " + state, "");
        }
    }

    @Override
    public Item getItem(Long id) {
        //getUser(userId);
        return itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ITEM, id));
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER, userId));
    }

    @Override
    public Booking get(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(BOOKING, id));
    }

    @Override
    public Map<String, InfoBookingDto> findLastAndNextBookings(Long itemId) {
        final List<Booking> bookings = repository.findAllByItemIdOrderByStart(itemId);
        final Map<String, InfoBookingDto> lastAndNext = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        for (int i = bookings.size() - 1; i >= 0; i--) {
            if (bookings.get(i).getEnd().isBefore(now)) {
                lastAndNext.put("last", new InfoBookingDto(bookings.get(i).getId(),
                        bookings.get(i).getBooker().getId()));
                try {
                    lastAndNext.put("next", new InfoBookingDto(bookings.get(i + 1).getId(),
                            bookings.get(i + 1).getBooker().getId()));
                } catch (IndexOutOfBoundsException ignored) {
                    //
                }
                return lastAndNext;
            }
        }
        try {
            if (bookings.get(0).getStart().isBefore(now)) {
                lastAndNext.put("last", new InfoBookingDto(bookings.get(0).getId(),
                        bookings.get(0).getBooker().getId()));
            }
        } catch (IndexOutOfBoundsException ignored) {
            //
        }
        return lastAndNext;
    }
}
