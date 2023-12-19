package ru.practicum.shareit.user.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.BookerDto;

@UtilityClass
public class BookerDtoMapper {
    public BookerDto toBookerDto(User user) {
        return new BookerDto(user.getId());
    }
}
