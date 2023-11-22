package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("update Booking b SET status = ?1 where b.id = ?2")
    void changeStatus(Status status, Long id);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);
    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByItemIdInAndStatusOrderByStartDesc(Set<Long> itemsIds, Status status);

    Collection<Booking> findAllByItemIdInOrderByStartDesc(Set<Long> itemsIds);

    List<Booking> findAllByItemIdOrderByStart(Long itemId);

    Optional<Booking> findFirstByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime time);

    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    Collection<Booking> findAllByItemIdInAndEndBeforeOrderByStartDesc(Set<Long> itemsIds, LocalDateTime time);

    Collection<Booking> findAllByItemIdInAndStatusInOrderByStartDesc(Set<Long> itemsIds, Status... statuses);

    Collection<Booking> findAllByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Status... statuses);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end ")
    Collection<Booking> findCurrentBookings(Long bookerId, LocalDateTime time);

    @Query("select b from Booking b " +
            "where b.item.id in :itemsIds " +
            "and :time between b.start and b.end " +
            "order by b.start desc")
    Collection<Booking> findCurrentOwnerBookings(@Param("itemsIds") Set<Long> itemsIds,
                                                 @Param("time") LocalDateTime time);

}
