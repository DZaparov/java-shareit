package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceImplIT {
    @Autowired
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void approveBookingTest() {
        User user1 = new User(null, "Elon", "elon@spacex.com");
        User user2 = new User(null, "Bill", "bill@microsoft.com");

        UserDto userDto1 = userService.createUser(user1);
        UserDto userDto2 = userService.createUser(user2);

        ItemDto itemDto1 = new ItemDto(null, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        ItemDto itemDto2 = new ItemDto(null, "ПерформаторNEW", "Новый перфоратор", true, null, null);
        ItemDto itemDto3 = new ItemDto(null, "Гвоздь", "Остался лишний", true, null, null);

        itemDto1 = itemService.createItem(itemDto1, userDto1.getId());
        itemDto2 = itemService.createItem(itemDto2, userDto2.getId());
        itemDto3 = itemService.createItem(itemDto3, userDto1.getId());

        BookingDto bookingDto1 = new BookingDto(null, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(7),
                itemDto1.getId(), null, null, null, BookingStatus.WAITING);
        BookingDto bookingDto2 = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7),
                itemDto1.getId(), null, null, null, BookingStatus.WAITING);
        BookingDto bookingDto3 = new BookingDto(null, LocalDateTime.now().minusDays(6), LocalDateTime.now().minusDays(2),
                itemDto3.getId(), null, null, null, BookingStatus.WAITING);
        BookingDto bookingDto4 = new BookingDto(null, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4),
                itemDto3.getId(), null, null, null, BookingStatus.WAITING);

        BookingDto bookingDto5 = new BookingDto(null, LocalDateTime.now().plusDays(8), LocalDateTime.now().plusDays(10),
                itemDto2.getId(), null, null, null, BookingStatus.WAITING);

        bookingDto1 = bookingService.createBooking(bookingDto1, userDto2.getId());
        bookingDto2 = bookingService.createBooking(bookingDto2, userDto2.getId());
        bookingDto3 = bookingService.createBooking(bookingDto3, userDto2.getId());
        bookingDto4 = bookingService.createBooking(bookingDto4, userDto2.getId());
        bookingDto5 = bookingService.createBooking(bookingDto5, userDto1.getId());

        bookingDto3.setStatus(BookingStatus.APPROVED);

        BookingDto result3 = bookingService.approveBooking(bookingDto3.getId(), userDto1.getId(), true);

        assertThat(bookingDto3.getId(), equalTo(result3.getId()));
        assertThat(bookingDto3.getStatus(), equalTo(result3.getStatus()));
    }
}
