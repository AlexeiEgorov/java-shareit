package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ItemRequestResponseDto {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private List<ItemForRequestDto> items;
}
