package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageRequest);

    @Query("select i from Item i " +
            "where (UPPER(i.name) like ?1 or UPPER(i.description) like ?1) " +
            "and i.available = true")
    Page<Item> findItemsByText(String text, Pageable pageRequest);

    List<Item> findAllByRequestIdIn(Set<Long> requestsIds);
}