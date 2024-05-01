package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private Long id = 0L;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        generateItemId(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long id, Item item) {
        if (items.containsKey(id)) {
            item.setId(id);
            items.put(id, item);
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
        return item;
    }

    @Override
    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь не найдена");
        }
    }

    @Override
    public List<Item> listItemsOfUser(Long ownerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (ownerId.equals(item.getOwner().getId())) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void generateItemId(Item item) {
        if (item.getId() == null) {
            item.setId(++id);
        }
    }
}
