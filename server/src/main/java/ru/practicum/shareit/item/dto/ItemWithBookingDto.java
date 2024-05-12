package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class ItemWithBookingDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private ItemRequest request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}

