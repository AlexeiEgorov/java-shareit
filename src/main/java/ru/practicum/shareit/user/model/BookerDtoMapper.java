package ru.practicum.shareit.user.model;

import ru.practicum.shareit.user.dto.BookerDto;

public class BookerDtoMapper {
    public static BookerDto toBookerDto(User user) {
        return new BookerDto(user.getId());
    }
}
