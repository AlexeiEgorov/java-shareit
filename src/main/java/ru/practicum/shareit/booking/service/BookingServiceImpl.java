package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConstraintViolationException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
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
    public Collection<Booking> getUserBookings(Long userId, State state) {
        getUser(userId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return repository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return repository.findCurrentBookings(userId, now);
            case FUTURE:
                return repository.findAllByBookerIdAndStatusInOrderByStartDesc(userId, Status.WAITING, Status.APPROVED);
            case WAITING:
                return repository.findAllByBookerIdAndStatus(userId, Status.WAITING, Sort.by(Sort.Direction.DESC,
                        "start"));
            case REJECTED:
                return repository.findAllByBookerIdAndStatus(userId, Status.REJECTED, Sort.by(Sort.Direction.DESC,
                    "start"));
            default:
                return repository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
        }
    }

    @Override
    public Collection<Booking> getOwnerItemsBookings(State state, Set<Long> itemsIds) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return repository.findAllByItemIdInOrderByStartDesc(itemsIds);
            case CURRENT:
                return repository.findCurrentOwnerBookings(itemsIds, now);
            case FUTURE:
                return repository.findAllByItemIdInAndStatusInOrderByStartDesc(itemsIds, Status.APPROVED,
                        Status.WAITING);
            case WAITING:
                return repository.findAllByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.WAITING);
            case REJECTED:
                return repository.findAllByItemIdInAndStatusOrderByStartDesc(itemsIds, Status.REJECTED);
            default:
                return repository.findAllByItemIdInAndEndBeforeOrderByStartDesc(itemsIds, now);
        }
    }

    @Override
    public Item getItem(Long id) {
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
    public Map<Long, Map<String, Booking>> findLastAndNextBookings(Set<Long> itemsIds) {
        Map<Long, List<Booking>> itemsBookings = repository.findAllByItemIdIn(itemsIds, Sort.by(Sort.Direction.ASC,
                        "start"))
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
        final Map<Long, Map<String, Booking>> itemsLastAndNextBookings = new HashMap<>();
        final LocalDateTime now = LocalDateTime.now();

        outer:
        for (Map.Entry<Long, List<Booking>> e : itemsBookings.entrySet()) {
            List<Booking> bookings = e.getValue();
            Long item = e.getKey();
            itemsLastAndNextBookings.put(item, new HashMap<>());

            for (int i = bookings.size() - 1; i >= 0; i--) {
                if (bookings.get(i).getEnd().isBefore(now)) {
                    itemsLastAndNextBookings.get(item).put("last", bookings.get(i));
                    try {
                        itemsLastAndNextBookings.get(item).put("next", bookings.get(i + 1));
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                    continue outer;
                }
            }
            try {
                if (bookings.get(0).getStart().isBefore(now)) {
                    itemsLastAndNextBookings.get(item).put("last", bookings.get(0));
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return itemsLastAndNextBookings;
    }
}
