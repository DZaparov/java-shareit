package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    public final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса: {}, владелец id={}", itemRequestDto, ownerId);
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto, ownerId);
        log.info("Создан запрос: {}", result);

        return result;
    }

    @GetMapping
    public List<ItemRequestDto> getMyItemRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка получения списка запросов владельца id={}", ownerId);
        List<ItemRequestDto> result = itemRequestService.getMyItemRequests(ownerId);
        log.info("Получен список запросов. Количество: {}", result.size());
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.info("Попытка получения запроса пользователя id={}", userId);
        List<ItemRequestDto> result = itemRequestService.getUserItemRequests(userId, from, size);
        log.info("Получен запрос: {}", result);
        return result;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка получения запроса id={}", requestId);
        ItemRequestDto result = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Получен запрос: {}", result);
        return result;
    }
}
