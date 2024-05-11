package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    public final BookingClient bookingService;

    public BookingController(BookingClient bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookItemRequestDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка создания запроса на создание бронирования: {}, владелец id={}", bookingDto, userId);
        ResponseEntity<Object> result = bookingService.bookItem(userId, bookingDto);
        log.info("Создан запрос создания бронирования: {}", result);

        return result;
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на изменение статуса подтверждения на {} бронирования id=: {}, владелец id={}",
                approved, bookingId, ownerId);
        ResponseEntity<Object> result = bookingService.approveBooking(bookingId, ownerId, approved);
        log.info("Создан запрос изменения статуса бронирования: {}", result);

        return result;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка создания запроса на получение бронирования id=: {}, пользователем id={}",
                bookingId, userId);
        ResponseEntity<Object> result = bookingService.getBooking(userId, bookingId);
        log.info("Создан запрос на получение бронирования: {}", result);

        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка создания запроса на получение списка всех бронирований текущего пользователя id=: {}, статус={}",
                userId, state);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Такой статус не поддерживается"));
        ResponseEntity<Object> result = bookingService.getBookings(userId, bookingState, from, size);
        log.info("Создан запрос на получение списка: {}", result);

        return result;
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                                   @RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка создания запроса на получение списка всех бронирований владельца id=: {}, статус={}",
                userId, state);
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Такой статус не поддерживается"));
        ResponseEntity<Object> result = bookingService.getOwnerBookings(userId, bookingState, from, size);
        log.info("Создан запрос на получение списка: {}", result);

        return result;
    }
}
