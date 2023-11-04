package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemPatchDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
}