package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    public final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания вещи: {}, владелец id={}", itemDto, ownerId);
        ItemDto result = itemService.createItem(itemDto, ownerId);
        log.info("Создана вещь: {}", result);

        return result;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long id,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка обновления вещи {}, id={}, владелец id={}", itemDto, id, ownerId);
        ItemDto result = itemService.updateItem(id, itemDto, ownerId);
        log.info("Обновлена вещь: {}", result);

        return result;
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto getItem(@PathVariable Long id,
                                      @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка получения вещи id={}", id);
        ItemWithBookingDto result = itemService.getItemById(id, ownerId);
        log.info("Получена вещь: {}", result);
        return result;
    }

    @GetMapping
    public List<ItemWithBookingDto> listItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка получения списка вещей владельца id={}", ownerId);
        List<ItemWithBookingDto> result = itemService.listItemsOfUser(ownerId, from, size);
        log.info("Получен список вещей. Количество: {}", result.size());
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка поиска вещи по запросу: {}", text);
        List<ItemDto> result = itemService.searchItem(text, from, size);
        log.info("Получен список вещей. Количество: {}", result.size());

        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto comment,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId) {
        return itemService.addComment(comment, userId, itemId);
    }
}
