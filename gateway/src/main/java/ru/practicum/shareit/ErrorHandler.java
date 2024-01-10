package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.model.ErrorResponse;
import ru.practicum.shareit.model.ValidationErrorResponse;
import ru.practicum.shareit.model.Violation;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(Exception e) {
        if (e instanceof ConstraintViolationException) {
            ConstraintViolationException exp = (ConstraintViolationException) e;
            final List<Violation> violations = exp.getConstraintViolations().stream()
                    .map(violation -> new Violation(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()))
                    .collect(Collectors.toList());
            log.debug("Получен статус 400 Bad request (ConstraintValidationException); violations: {}", violations);
            return new ValidationErrorResponse(violations);

        } else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exp = (MethodArgumentNotValidException) e;
            final List<Violation> violations = exp.getBindingResult().getFieldErrors().stream()
                    .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());
            log.debug("Получен статус 400 Bad request (MethodArgumentNotValidException); violations: {}", violations);
            return new ValidationErrorResponse(violations);
        }
        return new ValidationErrorResponse(List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onIllegalArgumentException(RuntimeException e) {
        if (e instanceof IllegalArgumentException) {
            log.debug("Получен статус 400 Bad request (IllegalArgumentException): {}", e.getMessage());
            return new ErrorResponse(e.getMessage(), "");
        }
        log.debug("Получен статус 400 Bad request (IllegalArgumentException): {}", e.getMessage());
        return new ErrorResponse(e.toString(), "возникла непредвиденная ошибка ограничения " +
                "переданного параметра");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse onUnexpectedError(Throwable e) {
        log.debug("Получен статус 500 Internal server error {}", e.getMessage(), e);
        return new ErrorResponse(e.toString(), "возникла непредвиденная ошибка");
    }
}
