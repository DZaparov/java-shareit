package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Вещь с идентификатором " + bookingDto.getItemId() + " не найдена."));

        if (!item.isAvailable()) {
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
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Бронирование с идентификатором " + id + " не найдено."));

        if (!(userId.equals(booking.getBooker().getId()) ||
                userId.equals(booking.getItem().getOwner().getId()))) {
            throw new NotFoundException("Бронирование может посмотреть только его владелец или автор");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long bookerId, String state, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        User booker = userRepository.findById(bookerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + bookerId + " не найден."));

        Page<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByEndDesc(bookerId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(bookerId, now, now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, now, page);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.valueOf(state), page);
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
    public List<BookingDto> getOwnerBookings(Long ownerId, String state, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + ownerId + " не найден."));

        Page<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByEndDesc(ownerId, page);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now, page);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now, page);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(ownerId, now, page);
                break;
            case "WAITING":
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.valueOf(state), page);
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
