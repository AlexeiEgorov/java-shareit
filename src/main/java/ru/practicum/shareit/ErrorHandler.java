package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.model.ErrorResponse;
import ru.practicum.shareit.model.ValidationErrorResponse;
import ru.practicum.shareit.model.Violation;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.Constants.*;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()))
                .collect(Collectors.toList());
        log.debug("Получен статус 400 Bad request (ConstraintValidationException); violations: {}", violations);
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.debug("Получен статус 400 Bad request (MethodArgumentNotValidException); violations: {}", violations);
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse onCustomConstraintViolationException(
            ru.practicum.shareit.exception.ConstraintViolationException e) {
        log.debug("Получен статус 400 Conflict; value:{}", e.getValue(), e);
        return new ErrorResponse(String.format("Значение ошибки - (%s)", e.getValue()), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ErrorResponse onNotAllowedActionException(NotAllowedActionException e) {
        log.debug("Получен статус 400 Conflict; value:{}", e.getValue(), e);
        return new ErrorResponse(e.getMessage(), e.getValue());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onEntityNotFoundException(EntityNotFoundException e) {
        log.debug("Получен статус 404 Not found; entityClass:{};value:{}", e.getEntityClass(), e.getValue(), e);
        if (USER.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Пользователь с данным id, не зарегестрирован");
        } else if (ITEM.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Предмет с данным id, не зарегестрирован");
        } else if (BOOKING.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Бронирование с данным id, не зарегестрировано");
        } else if (NO_ACCESS.equals(e.getEntityClass())) {
            return new ErrorResponse(String.format("id - (%s)", e.getValue()),
                    "Доступ к предмету с данным id, недоступен");
        }
        return new ErrorResponse((String.format("объект (%s); значение (%s)", e.getEntityClass(), e.getValue())),
                "неизвестная ошибка");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse onDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.debug("Получен статус 409 Conflict (); причина: {}", e.toString());
        return new ErrorResponse("Нарушение интеграции данных", e.getCause().getCause().getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onUnexpectedError(Throwable e) {
        System.out.println(e.getMessage());
        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
        return new ErrorResponse(e.toString(), "возникла непредвиденная ошибка");
    }
}
