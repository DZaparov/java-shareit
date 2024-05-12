package ru.practicum.shareit.exception;

public class BlankFieldException extends RuntimeException {
    public BlankFieldException(final String message) {
        super(message);
    }
}
