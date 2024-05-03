package ru.practicum.shareit.exception;

public class WrongParamException extends RuntimeException {
    public WrongParamException(final String message) {
        super(message);
    }
}
