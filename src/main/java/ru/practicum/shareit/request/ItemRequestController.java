package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService service;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestResponseDto add(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return ItemRequestMapper.toDto(service.add(userId, itemRequestDto));
    }

    @GetMapping
    public List<ItemRequestResponseDto> getUserRequests(@RequestHeader(USER_ID_REQ_HEADER) Long userId) {
        return makeRequestsDtoAndLoadWithItemsDto(service.getUserRequests(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllNonOwnedRequests(@RequestHeader(USER_ID_REQ_HEADER) Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive  Integer size) {
        return makeRequestsDtoAndLoadWithItemsDto(service.getAllNonOwnedRequests(userId, from, size).getContent());
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto get(@RequestHeader(USER_ID_REQ_HEADER) Long userId, @PathVariable Long requestId) {
        return makeRequestsDtoAndLoadWithItemsDto(List.of(service.get(userId, requestId))).get(0);
    }

    private List<ItemRequestResponseDto> makeRequestsDtoAndLoadWithItemsDto(Collection<ItemRequest> itemRequests) {
        Map<Long, List<ItemForRequestDto>> itemsByRequestsIds = itemService.findItemsByRequestsIds(itemRequests.stream()
                        .map(ItemRequest::getId)
                        .collect(Collectors.toSet())
                ).stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(groupingBy(ItemForRequestDto::getRequestId));

        return itemRequests.stream()
                .map(itemRequest -> {
                    ItemRequestResponseDto itemRequestDto = ItemRequestMapper.toDto(itemRequest);
                    itemRequestDto.setItems(itemsByRequestsIds.getOrDefault(itemRequest.getId(), List.of()));
                    return itemRequestDto;
                }).collect(Collectors.toList());
    }
}
