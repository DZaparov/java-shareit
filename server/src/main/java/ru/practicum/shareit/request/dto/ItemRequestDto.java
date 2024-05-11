package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto implements Comparable<ItemRequestDto> {
    private Long id;
    @NotBlank
    private String description;
    private UserDto requestorDto;
    private LocalDateTime created;
    private List<ItemResponseDto> items;

    public ItemRequestDto(Long id, String description, UserDto requestorDto, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestorDto = requestorDto;
        this.created = created;
    }

    @Override
    public int compareTo(ItemRequestDto o) {
        return (this.created.compareTo(o.getCreated()));
    }
}
