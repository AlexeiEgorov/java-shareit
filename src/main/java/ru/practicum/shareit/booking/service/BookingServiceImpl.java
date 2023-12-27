package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Page<Booking> getUserBookings(Long userId, State state, Integer from, Integer size) {
        getUser(userId);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(SORT_START_PARAM).descending());
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return repository.findAllByBookerId(userId, pageRequest);
            case CURRENT:
                pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
                return repository.findCurrentBookings(userId, now, pageRequest);
            case FUTURE:
                return repository.findAllByBookerIdAndStatusIn(userId, pageRequest, Status.WAITING, Status.APPROVED);
            case WAITING:
                return repository.findAllByBookerIdAndStatus(userId, Status.WAITING, pageRequest);
            case REJECTED:
                return repository.findAllByBookerIdAndStatus(userId, Status.REJECTED, pageRequest);
            default:
                return repository.findAllByBookerIdAndEndBefore(userId, now, pageRequest);
        }
    }

    @Override
    public Page<Booking> getOwnerItemsBookings(Long userId, State state, Integer from, Integer size) {
        getUser(userId);

        LocalDateTime now = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(SORT_START_PARAM).descending());
        switch (state) {
            case ALL:
                return repository.findOwnerBookings(userId, pageRequest);
            case CURRENT:
                return repository.findOwnerCurrentBookings(userId, now, pageRequest);
            case FUTURE:
                return repository.findOwnerBookingsByStatuses(userId, pageRequest,
                        Status.APPROVED, Status.WAITING);
            case WAITING:
                return repository.findOwnerBookingsByStatuses(userId, pageRequest, Status.WAITING);
            case REJECTED:
                return repository.findOwnerBookingsByStatuses(userId, pageRequest, Status.REJECTED);
            default:
                return repository.findOwnerPastBookings(userId, now, pageRequest);
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
    public Map<Long, List<Booking>> findLastAndNextBookings(Set<Long> itemsIds) {
        return repository.findAllByItemIdIn(itemsIds, Sort.by(SORT_START_PARAM).ascending())
                .stream()
                .collect(groupingBy(booking -> booking.getItem().getId(), toList()));
    }
}
