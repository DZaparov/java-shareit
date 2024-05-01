package ru.practicum.shareit.exception;

public class ApproveRequestException extends RuntimeException {
    public ApproveRequestException(final String message) {
        super(message);
    }
}