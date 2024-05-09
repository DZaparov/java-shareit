package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BlankFieldException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;
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
public class ItemServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item expectedItem = ItemMapper.toItem(itemDto, user, null);

        when(itemRepository.save(any())).thenReturn(expectedItem);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ItemDto createdItemDto = itemService.createItem(itemDto, user.getId());

        assertEquals(ItemMapper.toItemDto(expectedItem), createdItemDto);
        verify(itemRepository, Mockito.times(1)).save(expectedItem);
    }

    @Test
    void createItemWithBlankFieldTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        assertThrows(BlankFieldException.class, () -> itemService.createItem(itemDto, null));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItemWithNonexistentUserTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 99L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void createItemWithRequestTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, 1L);
        User user = new User(1L, "Elon", "elon@spacex.com");
        User requestor = new User(2L, "Bill", "bill@microsoft.com");
        ItemRequest itemRequest = new ItemRequest(1L, "Нужен перфоратор", requestor, LocalDateTime.now());

        Item expectedItem = ItemMapper.toItem(itemDto, user, itemRequest);

        when(itemRepository.save(any())).thenReturn(expectedItem);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));

        ItemDto createdItemDto = itemService.createItem(itemDto, user.getId());

        assertEquals(ItemMapper.toItemDto(expectedItem), createdItemDto);
        verify(itemRepository, Mockito.times(1)).save(expectedItem);
    }

    @Test
    void createItemWithNonexistentRequestTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, 1L);
        User user = new User(1L, "Elon", "elon@spacex.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, user.getId()));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemTest() {
        ItemDto itemDto = new ItemDto(1L, "ПерфораторNew", "Новый мощный инструмент для ремонта", true, null, null);

        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);

        Item expectedItem = ItemMapper.toItem(itemDto, user, null);

        when(itemRepository.save(any())).thenReturn(expectedItem);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ItemDto updatedItemDto = itemService.updateItem(1L, itemDto, user.getId());

        assertEquals(ItemMapper.toItemDto(expectedItem), updatedItemDto);
        verify(itemRepository, Mockito.times(1)).save(expectedItem);
    }

    @Test
    void updateItemWithBlankFieldTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        assertThrows(BlankFieldException.class, () -> itemService.updateItem(1L, itemDto, null));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemWithNonexistentUserTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 99L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemWithNonexistentItemTest() {
        ItemDto itemDto = new ItemDto(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null, null);
        User user = new User(1L, "Elon", "elon@spacex.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, itemDto, 1L));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItemNotByOwnerTest() {
        ItemDto itemDto = new ItemDto(1L, "ПерфораторNew", "Новый мощный инструмент для ремонта", true, null, null);

        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(1L, itemDto, user2.getId()));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemByIdTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, null);

        List<Booking> bookings = new ArrayList<>();

        Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(8), item, user, BookingStatus.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(6), item, user, BookingStatus.REJECTED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item, user, BookingStatus.APPROVED);
        Booking booking4 = new Booking(4L, LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(6), item, user, BookingStatus.REJECTED);

        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);
        bookings.add(booking4);

        ItemWithBookingDto expectedItem = new ItemWithBookingDto(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), item.getRequest(), BookingMapper.toBookingDto(booking1),
                BookingMapper.toBookingDto(booking3), new ArrayList<>());


        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemId(item.getId())).thenReturn(bookings);

        ItemWithBookingDto resultItemDto = itemService.getItemById(item.getId(), user.getId());

        assertEquals(expectedItem, resultItemDto);
    }

    @Test
    void getListItemsOfUserTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");

        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Item item2 = new Item(2L, "Лопата", "Просто копать ямы", true, null);
        Item item3 = new Item(3L, "Молоток", "Забивать гвозди", true, null);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        Page<Item> itemsPage = new PageImpl<>(items);

        ItemWithBookingDto itemWithBookingDto1 = new ItemWithBookingDto(item1.getId(), item1.getName(), item1.getDescription(), item1.isAvailable(), item1.getRequest(), null, null, new ArrayList<>());
        ItemWithBookingDto itemWithBookingDto2 = new ItemWithBookingDto(item2.getId(), item2.getName(), item2.getDescription(), item2.isAvailable(), item2.getRequest(), null, null, new ArrayList<>());
        ItemWithBookingDto itemWithBookingDto3 = new ItemWithBookingDto(item3.getId(), item3.getName(), item3.getDescription(), item3.isAvailable(), item3.getRequest(), null, null, new ArrayList<>());

        List<ItemWithBookingDto> expected = new ArrayList<>();
        expected.add(itemWithBookingDto1);
        expected.add(itemWithBookingDto2);
        expected.add(itemWithBookingDto3);

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));
        when(itemRepository.findById(item3.getId())).thenReturn(Optional.of(item3));

        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(itemsPage);

        List<ItemWithBookingDto> result = itemService.listItemsOfUser(user.getId(), 0, 10);

        assertEquals(expected, result);
    }

    @Test
    void searchItemTest() {
        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, null);
        Page<Item> itemsPage = new PageImpl<>(List.of(item1));

        when(itemRepository.searchItem(anyString(), any(Pageable.class))).thenReturn(itemsPage);

        List<ItemDto> result = itemService.searchItem("Перф", 0, 10);

        assertEquals(List.of(ItemMapper.toItemDto(item1)), result);
    }

    @Test
    void addCommentTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        Item item = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user1, null);

        Booking booking1 = new Booking(item.getId(), LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), item, user2, BookingStatus.APPROVED);
        Booking booking2 = new Booking(item.getId(), LocalDateTime.now().minusDays(8), LocalDateTime.now().minusDays(7), item, user2, BookingStatus.APPROVED);
        Booking booking3 = new Booking(item.getId(), LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(4), item, user2, BookingStatus.APPROVED);

        List<Booking> expected = new ArrayList<>();
        expected.add(booking1);
        expected.add(booking2);
        expected.add(booking3);

        CommentDto commentDto = new CommentDto(1L, "Крутой перфоратор", ItemMapper.toItemDto(item), UserMapper.toUserDto(user2), user2.getName(), LocalDateTime.now());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(anyLong(), any(LocalDateTime.class))).thenReturn(expected);
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, item, user2));

        CommentDto result = itemService.addComment(commentDto, user2.getId(), item.getId());

        assertEquals(commentDto, result);
        verify(commentRepository, Mockito.times(1)).save(any());
    }
}
