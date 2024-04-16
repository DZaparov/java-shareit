package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NullException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new NullException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        if (itemDto.getName() == null) {
            throw new NullException("Поле name не должно быть пустым");
        } else {
            if (itemDto.getName().isBlank()) {
                throw new NullException("Поле name не должно быть пустым");
            }
        }
        if (itemDto.getDescription() == null) {
            throw new NullException("Поле description не должно быть пустым");
        } else {
            if (itemDto.getDescription().isBlank()) {
                throw new NullException("Поле description не должно быть пустым");
            }
        }
        if (itemDto.getAvailable() == null) {
            throw new NullException("Поле available не должно быть пустым");
        }

        Item item = ItemMapper.toItem(itemDto);

        User owner = userStorage.getUserById(ownerId);
        item.setOwner(ownerId);

        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new NullException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }

        Item itemToUpdate = itemStorage.getItemById(id);
        Item item = ItemMapper.fillItem(itemDto, itemToUpdate);

        if (ownerId != itemToUpdate.getOwner()) {
            throw new ForbiddenException("Вещь может менять только ее владелец");
        }

        return ItemMapper.toItemDto(itemStorage.updateItem(id, item));
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> listItemsOfUser(Long ownerId) {
        return itemStorage.listItemsOfUser(ownerId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemStorage.searchItem(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}
