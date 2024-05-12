package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                                  ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        User owner = userRepository.findById(requestorId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + requestorId + " не найден."));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, owner);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getMyItemRequests(Long requestorId) {
        User owner = userRepository.findById(requestorId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + requestorId + " не найден."));

        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.getItemRequestsByRequestorId(requestorId)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .sorted(ItemRequestDto::compareTo)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequestIdIn(
                itemRequestsDto
                        .stream()
                        .map(ItemRequestDto::getId)
                        .collect(Collectors.toList()));

        for (ItemRequestDto itemRequestDto : itemRequestsDto) {
            itemRequestDto.setItems(items
                    .stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .map(ItemMapper::toItemResponseDto)
                    .collect(Collectors.toList()));
        }

        return itemRequestsDto;
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long requestorId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequestDto> itemRequestsDto = itemRequestRepository.findAllByRequestorIdNot(requestorId, page)
                .filter(itemRequest -> !itemRequest.getRequestor().getId().equals(requestorId))
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();

        List<Item> items = itemRepository.findAllByRequestIdIn(
                itemRequestsDto
                        .stream()
                        .map(ItemRequestDto::getId)
                        .collect(Collectors.toList()));

        for (ItemRequestDto itemRequestDto : itemRequestsDto) {
            itemRequestDto.setItems(items
                    .stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .map(ItemMapper::toItemResponseDto)
                    .collect(Collectors.toList()));
        }

        return itemRequestsDto;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long id, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        ItemRequest itemRequest = itemRequestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Запрос с идентификатором " + id + " не найден."));

        List<ItemResponseDto> itemsResponseDto = itemRepository.findAllByRequestId(id)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        itemRequestDto.setItems(itemsResponseDto);

        return itemRequestDto;
    }
}
