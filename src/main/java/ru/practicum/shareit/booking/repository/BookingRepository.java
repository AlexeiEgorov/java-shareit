package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("update Booking b SET status = ?1 where b.id = ?2")
    void changeStatus(Status status, Long id);

    Page<Booking> findAllByBookerIdAndStatus(Long bookerId, Status status, Pageable pageReq);

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageReq);

    List<Booking> findAllByItemIdIn(Set<Long> itemsIds, Sort sort);

    boolean existsFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime time, Pageable pageReq);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.end < ?2")
    Page<Booking> findOwnerPastBookings(Long userId, LocalDateTime time, Pageable pageReq);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and b.status in ?2")
    Page<Booking> findOwnerBookingsByStatuses(Long userId, Pageable pageReq, Status... statuses);

    Page<Booking> findAllByBookerIdAndStatusIn(Long bookerId, Pageable pageReq, Status... statuses);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end ")
    Page<Booking> findCurrentBookings(Long bookerId, LocalDateTime time, Pageable pageReq);

    @Query("select b from Booking b where b.item.owner.id = ?1")
    Page<Booking> findOwnerBookings(Long userId, Pageable pageReq);

    @Query("select b from Booking b where b.item.owner.id = ?1 " +
            "and ?2 between b.start and b.end")
    Page<Booking> findOwnerCurrentBookings(Long userId, LocalDateTime time, Pageable pageReq);
}
