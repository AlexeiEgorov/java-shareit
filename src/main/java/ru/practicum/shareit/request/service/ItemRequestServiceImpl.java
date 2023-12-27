package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.Constants.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequest add(Long userId, ItemRequestDto itemRequestDto) {
        User requester = getUser(userId);
        return repository.save(ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build());
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        getUser(userId);
        return repository.findAllByRequesterId(userId, Sort.by(SORT_CREATED_PARAM).descending());
    }

    @Override
    public Page<ItemRequest> getAllNonOwnedRequests(Long userId, Integer from, Integer size) {
        getUser(userId);
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size,
                Sort.by(SORT_CREATED_PARAM).descending());
        return repository.findAllByRequesterIdIsNot(userId, pageRequest);
    }

    @Override
    public ItemRequest get(Long userId, Long requestId) {
        getUser(userId);
        return repository.findById(requestId).orElseThrow(() -> new EntityNotFoundException(REQUEST, requestId));
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER, userId));
    }
}
