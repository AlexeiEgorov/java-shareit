package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EmailAlreadyRegisteredException extends RuntimeException {
    private final String value;
}
