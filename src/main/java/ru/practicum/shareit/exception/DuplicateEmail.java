package ru.practicum.shareit.exception;

public class DuplicateEmail extends RuntimeException {
    public DuplicateEmail(final String message) {
        super(message);
    }
}