package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId);

    ItemWithBookingDto getItemById(Long id, Long ownerId);

    List<ItemWithBookingDto> listItemsOfUser(Long ownerId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(CommentDto comment, Long userId, Long itemId);
}
