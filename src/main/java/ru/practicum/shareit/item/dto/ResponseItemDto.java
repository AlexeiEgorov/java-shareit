package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseItemDto {
    private final Long id;
    private final String name;
    private final String description;
    private final boolean available;
}