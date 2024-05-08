package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    public final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка создания бронирования: {}, владелец id={}", bookingDto, userId);
        BookingDto result = bookingService.createBooking(bookingDto, userId);
        log.info("Создано бронирование: {}", result);

        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка изменения статуса подтверждения на {} бронирования id=: {}, владелец id={}",
                approved, bookingId, ownerId);
        BookingDto result = bookingService.approveBooking(bookingId, ownerId, approved);
        log.info("Статус бронирования изменен: {}", result);

        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка получения бронирования id=: {}, пользователем id={}",
                bookingId, userId);
        BookingDto result = bookingService.getBookingById(bookingId, userId);
        log.info("Бронирование получено: {}", result);

        return result;
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка получения списка всех бронирований текущего пользователя id=: {}, статус={}",
                userId, state);
        List<BookingDto> result = bookingService.getUserBookings(userId, state, from, size);
        log.info("Получен список: {}", result);

        return result;
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка получения списка всех бронирований владельца id=: {}, статус={}",
                userId, state);
        List<BookingDto> result = bookingService.getOwnerBookings(userId, state, from, size);
        log.info("Получен список: {}", result);

        return result;
    }
}
