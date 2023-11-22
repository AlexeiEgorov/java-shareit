package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotAllowedActionException extends RuntimeException {
    private final String value;

    public NotAllowedActionException(String message, String value) {
        super(message);
        this.value = value;
    }
}
