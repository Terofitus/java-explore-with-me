package error_handling;

import dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final MethodArgumentNotValidException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST.name(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ConstraintViolationException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST.name(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final IllegalArgumentException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.BAD_REQUEST.name(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleValidationException(final NotFoundException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.NOT_FOUND.name(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(final ConflictArgumentException e) {
        return new ApiError(Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString).collect(Collectors.toList()),
                e.getMessage(), e.getLocalizedMessage(),
                HttpStatus.CONFLICT.name(), LocalDateTime.now());
    }
}
