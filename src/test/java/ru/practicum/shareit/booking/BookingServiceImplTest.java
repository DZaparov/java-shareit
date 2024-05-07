package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user2, BookingStatus.APPROVED);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        BookingDto createdBookingDto = bookingService.createBooking(expectedBookingDto, user2.getId());

        assertEquals(expectedBookingDto, createdBookingDto);

        verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void createBookingWithBlankFieldTest() {
        assertThrows(BlankFieldException.class, () -> bookingService.createBooking(new BookingDto(), null));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWithWrongDateTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(10), item, user2, BookingStatus.APPROVED);
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);

        assertThrows(BookingDateException.class, () -> bookingService.createBooking(expectedBookingDto, user2.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWithWrongDateTest2() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(7), item, user2, BookingStatus.APPROVED);
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);

        assertThrows(BookingDateException.class, () -> bookingService.createBooking(expectedBookingDto, user2.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWithNotAvailableItemTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", false, user1, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user2, BookingStatus.APPROVED);
        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(expectedBookingDto, user2.getId()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBookingWithNonexistentUserTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.APPROVED);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDto, 99L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveBookingTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, booking.getStart(), booking.getEnd(), item, user, BookingStatus.APPROVED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto expectedBookingDto = BookingMapper.toBookingDto(expectedBooking);

        BookingDto approvedBookingDto = bookingService.approveBooking(booking.getId(), user.getId(), true);

        assertEquals(expectedBookingDto, approvedBookingDto);

        verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void approveBookingWithNonexistentUserTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.APPROVED);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(bookingDto.getId(), 99L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void rejectBookingTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.WAITING);
        Booking expectedBooking = new Booking(1L, booking.getStart(), booking.getEnd(), item, user, BookingStatus.REJECTED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto expectedBookingDto = BookingMapper.toBookingDto(expectedBooking);

        BookingDto approvedBookingDto = bookingService.approveBooking(booking.getId(), user.getId(), false);

        assertEquals(expectedBookingDto, approvedBookingDto);

        verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void approveBookingWithBlankFieldTest() {
        assertThrows(BlankFieldException.class, () -> bookingService.approveBooking(1L, null, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approveAlreadyApprovedBookingTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.APPROVED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(ApproveRequestException.class, () -> bookingService.approveBooking(booking.getId(), user.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingByIdTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user, BookingStatus.APPROVED);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto expectedBookingDto = BookingMapper.toBookingDto(booking);
        BookingDto resultBookingDto = bookingService.getBookingById(booking.getId(), user.getId());

        assertEquals(expectedBookingDto, resultBookingDto);
    }

    @Test
    void getBookingByIdWithBlankFieldTest() {
        assertThrows(BlankFieldException.class, () -> bookingService.getBookingById(1L, null));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingByIdByNotOwnerTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item, user1, BookingStatus.APPROVED);

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), user2.getId()));
    }

    @Test
    void getUserBookingsWithAllStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByEndDesc(anyLong(), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "ALL", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getUserBookingsWithCurrentStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(7), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(2), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "CURRENT", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getUserBookingsWithPastStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(2), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "PAST", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getUserBookingsWithFutureStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(17), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "FUTURE", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getUserBookingsWithWaitingStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(17), item1, user, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12), item2, user, BookingStatus.WAITING);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdAndStatusOrderByEndDesc(anyLong(), any(BookingStatus.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "WAITING", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getUserBookingsWithUnsupportedStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getUserBookings(user.getId(), "GOOD", 0, 10));
    }

    @Test
    void getUserBookingsWithWrongParamsTest() {
        assertThrows(WrongParamException.class, () -> bookingService.getUserBookings(1L, "ALL", -1, 10));
    }

    @Test
    void getOwnerBookingsWithAllStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByEndDesc(anyLong(), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "ALL", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getOwnerBookingsWithCurrentStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(7), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(2), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "CURRENT", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getOwnerBookingsWithPastStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(2), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "PAST", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getOwnerBookingsWithFutureStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(17), item1, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12), item2, user, BookingStatus.APPROVED);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByEndDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "FUTURE", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getOwnerBookingsWithWaitingStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(17), item1, user, BookingStatus.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(12), item2, user, BookingStatus.WAITING);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        Page<Booking> bookingsPage = new PageImpl<>(bookings);

        List<BookingDto> expected = new ArrayList<>();
        expected.add(BookingMapper.toBookingDto(booking1));
        expected.add(BookingMapper.toBookingDto(booking2));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByEndDesc(anyLong(), any(BookingStatus.class), any(Pageable.class))).thenReturn(bookingsPage);

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "WAITING", 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void getOwnerBookingsWithUnsupportedStateTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getOwnerBookings(user.getId(), "GOOD", 0, 10));
    }

    @Test
    void getOwnerBookingsWithWrongParamsTest() {
        assertThrows(WrongParamException.class, () -> bookingService.getOwnerBookings(1L, "ALL", -1, 10));
    }
}
