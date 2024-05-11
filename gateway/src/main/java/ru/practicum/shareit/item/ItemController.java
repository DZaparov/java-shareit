package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    public final ItemClient itemService;

    public ItemController(ItemClient itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на создание вещи: {}, владелец id={}", itemDto, ownerId);
        ResponseEntity<Object> result = itemService.createItem(itemDto, ownerId);
        log.info("Создан запрос на создание вещи: {}", result);

        return result;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на обновление вещи {}, id={}, владелец id={}", itemDto, id, ownerId);
        ResponseEntity<Object> result = itemService.updateItem(id, itemDto, ownerId);
        log.info("Создан запрос на обновление вещи: {}", result);

        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable Long id,
                                      @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на получение вещи id={}", id);
        ResponseEntity<Object> result = itemService.getItemById(id, ownerId);
        log.info("Создан запрос на получение вещи: {}", result);
        return result;
    }

    @GetMapping
    public ResponseEntity<Object> listItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка создания запроса на получение списка вещей владельца id={}", ownerId);
        ResponseEntity<Object> result = itemService.listItemsOfUser(ownerId, from, size);
        log.info("Создан запрос на получение списка вещей: {}", result);
        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                    @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка создания запроса на поиск вещи по запросу: {}", text);
        ResponseEntity<Object> result = itemService.searchItem(ownerId, text, from, size);
        log.info("Создан запрос на получение списка вещей: {}", result);

        return result;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto comment,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId) {
        return itemService.addComment(comment, userId, itemId);
    }
}
