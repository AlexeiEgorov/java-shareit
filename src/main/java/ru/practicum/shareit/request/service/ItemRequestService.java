package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestService {
    ItemRequest add(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequest> getUserRequests(Long userId);

    Page<ItemRequest> getAllNonOwnedRequests(Long userId, Integer from, Integer size);

    ItemRequest get(Long userId, Long requestId);

    User getUser(Long userId);
}
