package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BlankFieldException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
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
public class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequestTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "нужен перфоратор", UserMapper.toUserDto(user), LocalDateTime.now());

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ItemRequestDto createdItemRequestDto = itemRequestService.createItemRequest(itemRequestDto, user.getId());

        assertEquals(itemRequestDto, createdItemRequestDto);
        verify(itemRequestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void createItemRequestWithBlankFieldTest() {
        assertThrows(BlankFieldException.class, () -> itemRequestService.createItemRequest(new ItemRequestDto(), null));
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void createItemRequesByNonexistentIdTest() {
        User user = new User(99L, "Elon", "elon@spacex.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(new ItemRequestDto(), user.getId()));
    }

    @Test
    void getMyItemRequestsTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");

        ItemRequest itemRequest1 = new ItemRequest(1L, "нужен перфоратор", user1, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2L, "нужен гвоздь", user1, LocalDateTime.now());

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);
        itemRequests.add(itemRequest2);

        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user2, itemRequest1);
        Item item2 = new Item(2L, "ПерформаторNEW", "Новый перфоратор", true, user2, itemRequest1);
        Item item3 = new Item(3L, "Гвоздь", "Остался лишний", true, user2, itemRequest2);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        List<ItemResponseDto> itemsResponseDto1 = new ArrayList<>();
        itemsResponseDto1.add(ItemMapper.toItemResponseDto(item1));
        itemsResponseDto1.add(ItemMapper.toItemResponseDto(item2));

        List<ItemResponseDto> itemsResponseDto2 = new ArrayList<>();
        itemsResponseDto2.add(ItemMapper.toItemResponseDto(item3));

        ItemRequestDto itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);

        itemRequestDto1.setItems(itemsResponseDto1);
        itemRequestDto2.setItems(itemsResponseDto2);

        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(itemRequestDto1);
        itemRequestsDto.add(itemRequestDto2);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRequestRepository.getItemRequestsByRequestorId(user1.getId())).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(items);

        List<ItemRequestDto> resultList = itemRequestService.getMyItemRequests(user1.getId());

        assertEquals(itemRequestsDto, resultList);
    }

    @Test
    void getUserItemRequestsTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Bill", "bill@microsoft.com");
        User user3 = new User(3L, "Steve", "steve@apple.com");

        ItemRequest itemRequest1 = new ItemRequest(1L, "нужен перфоратор", user3, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(2L, "нужен гвоздь", user3, LocalDateTime.now());

        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest1);
        itemRequests.add(itemRequest2);

        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user2, itemRequest1);
        Item item2 = new Item(2L, "ПерформаторNEW", "Новый перфоратор", true, user2, itemRequest1);
        Item item3 = new Item(3L, "Гвоздь", "Остался лишний", true, user2, itemRequest2);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);

        Page<ItemRequest> itemRequestsPage = new PageImpl<>(itemRequests);

        List<ItemResponseDto> itemsResponseDto1 = new ArrayList<>();
        itemsResponseDto1.add(ItemMapper.toItemResponseDto(item1));
        itemsResponseDto1.add(ItemMapper.toItemResponseDto(item2));

        List<ItemResponseDto> itemsResponseDto2 = new ArrayList<>();
        itemsResponseDto2.add(ItemMapper.toItemResponseDto(item3));

        ItemRequestDto itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1);
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2);

        itemRequestDto1.setItems(itemsResponseDto1);
        itemRequestDto2.setItems(itemsResponseDto2);

        List<ItemRequestDto> itemRequestsDto = new ArrayList<>();
        itemRequestsDto.add(itemRequestDto1);
        itemRequestsDto.add(itemRequestDto2);

        when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(itemRequestsPage);
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(items);

        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(user1.getId(), 0, 10);

        assertEquals(itemRequestsDto, result);
    }

    @Test
    void getItemRequestByIdTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");

        ItemRequest itemRequest = new ItemRequest(1L, "нужен перфоратор", user, LocalDateTime.now());

        Item item1 = new Item(1L, "Перфоратор", "Мощный инструмент для ремонта", true, user, itemRequest);
        Item item2 = new Item(2L, "ПерформаторNEW", "Новый перфоратор", true, user, itemRequest);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        List<ItemResponseDto> itemsResponseDto = new ArrayList<>();
        itemsResponseDto.add(ItemMapper.toItemResponseDto(item1));
        itemsResponseDto.add(ItemMapper.toItemResponseDto(item2));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        itemRequestDto.setItems(itemsResponseDto);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(items);

        ItemRequestDto resultItemRequestDto = itemRequestService.getItemRequestById(itemRequest.getId(), user.getId());

        assertEquals(itemRequestDto, resultItemRequestDto);
    }
}
