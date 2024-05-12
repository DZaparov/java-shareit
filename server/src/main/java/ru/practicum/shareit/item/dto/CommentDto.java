package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private ItemDto item;
    private UserDto author;
    private String authorName;
    private LocalDateTime created;
}
