package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long ownerId);

    @Query("select i from Item i " +
            "where (UPPER(i.name) like ?1 or UPPER(i.description) like ?1) " +
            "and i.available = true")
    Collection<Item> findItemsByText(String text);

    Collection<BookingItemDto> findAllByIdIn(Set<Long> itemsIds);

    Collection<BookingItemDto> findAlLByOwnerId(Long ownerId);
}
