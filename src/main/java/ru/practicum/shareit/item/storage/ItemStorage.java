package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Long id, Item item);

    Item getItemById(Long id);

    List<Item> listItemsOfUser(Long ownerId);

    List<Item> searchItem(String text);
}
