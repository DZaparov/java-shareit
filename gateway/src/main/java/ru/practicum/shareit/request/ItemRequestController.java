package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    public final ItemRequestClient itemRequestService;

    public ItemRequestController(ItemRequestClient itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на создание запроса: {}, владелец id={}", itemRequestDto, ownerId);
        ResponseEntity<Object> result = itemRequestService.createItemRequest(itemRequestDto, ownerId);
        log.info("Создан запрос создания запроса: {}", result);

        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemRequests(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Попытка создания запроса на получение списка запросов владельца id={}", ownerId);
        ResponseEntity<Object> result = itemRequestService.getMyItemRequests(ownerId);
        log.info("Создан запрос на получение списка запросов: {}", result);
        return result;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Попытка создания запроса на получение запроса пользователя id={}", userId);
        ResponseEntity<Object> result = itemRequestService.getUserItemRequests(userId, from, size);
        log.info("Создан запрос на получение запроса пользователя: {}", result);
        return result;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Попытка создания запроса на получение запроса id={}", requestId);
        ResponseEntity<Object> result = itemRequestService.getItemRequestById(requestId, userId);
        log.info("Создан запрос на получение запроса: {}", result);
        return result;
    }
}
