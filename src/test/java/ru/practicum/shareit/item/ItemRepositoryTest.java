package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository repository;

    @Test
    void searchItemTest() {
        Item item = new Item(
                null,
                "Дрель",
                "super1000turbo edition",
                true,
                null,
                null);

        assertNull(item.getId());
        repository.save(item);
        assertNotNull(item.getId());

        PageRequest page = PageRequest.of(0, 10);

        List<ItemDto> items = repository.searchItem("дрель", page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        assertEquals(1, items.size());
    }
}
