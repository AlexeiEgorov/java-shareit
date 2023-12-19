package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("update Booking b SET status = ?1 where b.id = ?2")
    void changeStatus(Status status, Long id);

    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Sort start);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemIdIn(Set<Long> itemsIds, Sort start);

    boolean existsFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.end < ?2")
    Collection<Booking> findOwnerPastBookings(Long userId, LocalDateTime time, Sort start);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status in ?2")
    Collection<Booking> findOwnerBookingsByStatuses(Long userId, Sort start, Status... statuses);

    Collection<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Status... statuses);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end ")
    Collection<Booking> findCurrentBookings(Long bookerId, LocalDateTime time);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Collection<Booking> findOwnerBookings(Long userId, Sort start);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and ?2 between b.start and b.end")
    Collection<Booking> findOwnerCurrentBookings(Long userId, LocalDateTime time, Sort start);
}
