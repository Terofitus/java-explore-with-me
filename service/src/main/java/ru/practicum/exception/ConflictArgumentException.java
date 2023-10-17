package ru.practicum.exception;

public class ConflictArgumentException extends RuntimeException {
    public ConflictArgumentException(String message) {
        super(message);
    }
}
