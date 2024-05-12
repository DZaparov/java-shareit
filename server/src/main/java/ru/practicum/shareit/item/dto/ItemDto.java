package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.request.dto.ItemRequestDto;

/**
 * TODO Sprint add-controllers.
 */
@Data
@ToString
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequestDto requestDto;
    private Long requestId;
}
