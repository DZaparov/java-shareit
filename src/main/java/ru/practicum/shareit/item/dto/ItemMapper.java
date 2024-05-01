package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    public static ItemWithBookingDto toItemWithBookingDto(
            Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> commentList) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                lastBooking,
                nextBooking,
                commentList
        );
    }

    public static Item fillItem(ItemDto itemDto, Item item) {
        Item result = new Item(
                itemDto.getId() == null ? item.getId() : itemDto.getId(),
                itemDto.getName() == null ? item.getName() : itemDto.getName(),
                itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription(),
                itemDto.getAvailable() == null ? item.isAvailable() : itemDto.getAvailable(),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
        result.setOwner(item.getOwner());
        return result;
    }
}