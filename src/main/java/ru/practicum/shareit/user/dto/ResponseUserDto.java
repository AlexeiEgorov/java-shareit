package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseUserDto {
    private final Long id;
    private final String name;
    private final String email;
}