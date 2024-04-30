package ru.practicum.shareit.exception;

public class BookingDateException extends RuntimeException {
    public BookingDateException(final String message) {
        super(message);
    }
}