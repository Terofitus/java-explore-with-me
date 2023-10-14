package ru.practicum.error_handling;

import dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final IllegalArgumentException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST.name(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleValidationException(final Throwable e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(), LocalDateTime.now());
    }
}