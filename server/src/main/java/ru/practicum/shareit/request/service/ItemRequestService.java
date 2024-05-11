package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long ownerId);

    List<ItemRequestDto> getMyItemRequests(Long ownerId);

    List<ItemRequestDto> getUserItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long id, Long userId);
}
