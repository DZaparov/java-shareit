package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        if (userId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BookingDateException("Дата конца бронирования раньше, чем дата начала бронирования");
        }

        if (bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new BookingDateException("Дата конца бронирования совпадает с датой начала бронирования");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с идентификатором " + bookingDto.getItemId() + " не найдена."));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        if (userId.equals(item.getOwner().getId())) {
            throw new NotFoundException("Пользователь не может бронировать свою вещь");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, user, item);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long id, Long ownerId, Boolean approved) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + ownerId + " не найден."));

        Booking bookingToUpdate = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирование с идентификатором " + id + " не найдено."));

        if (!ownerId.equals(bookingToUpdate.getItem().getOwner().getId())) {
            throw new NotFoundException("Бронирование может менять только его владелец");
        }

        if (approved) {
            if (bookingToUpdate.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ApproveRequestException("Бронирование уже подтверждено");
            }
            bookingToUpdate.setStatus(BookingStatus.APPROVED);
        } else {
            bookingToUpdate.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(bookingToUpdate));
    }

    @Override
    public BookingDto getBookingById(Long id, Long userId) {
        if (userId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        Booking bookingToUpdate = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирование с идентификатором " + id + " не найдено."));

        if (!(userId.equals(bookingToUpdate.getBooker().getId()) ||
                userId.equals(bookingToUpdate.getItem().getOwner().getId()))) {
            throw new NotFoundException("Бронирование может менять только его владелец или автор");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(bookingToUpdate));
    }

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, String state) {
        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + bookerId + " не найден."));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(bookerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(bookerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, now);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.valueOf(state));
                break;
            default:
                throw new UnsupportedStatusException("Такой статус не поддерживается");
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + ownerId + " не найден."));

        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByEndDesc(ownerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(ownerId, now);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.valueOf(state));
                break;
            default:
                throw new UnsupportedStatusException("Такой статус не поддерживается");
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
