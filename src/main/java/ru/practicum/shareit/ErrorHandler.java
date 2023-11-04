package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import javax.validation.ConstraintViolationException;

import static ru.practicum.shareit.Constants.ITEM;
import static ru.practicum.shareit.Constants.USER;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException e) {
        log.debug("Получен статус 400 Bad request; {}", e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.debug("Получен статус 404 Not found; entityClass:{};value:{}", e.getEntityClass(), e.getValue(), e);
        if (USER.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Пользователь с данным id, не зарегестрирован");
        } else if (ITEM.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Предмет с данным id, не зарегестрирован");
        }
        return new ErrorResponse((String.format("объект (%s); значение (%s)", e.getEntityClass(), e.getValue())),
                "неизвестная ошибка");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public  ErrorResponse handleConflictException(EmailAlreadyRegisteredException e) {
        log.debug("Получен статус 409 Conflict; value:{}", e.getValue(), e);
        return new ErrorResponse(String.format("email - (%s)", e.getValue()),
                "Данный email уже зарегестрирован за другим пользователем");
    }

    //@ExceptionHandler
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //public ErrorResponse handleUnexpectedError(Throwable e) {
    //    log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
    //    return new ErrorResponse(e.getMessage(), "возникла непредвиденная ошибка");
    //}
}
