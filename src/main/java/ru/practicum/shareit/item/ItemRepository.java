package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerId(Long ownerId, Pageable page);

    @Query("select i " +
            "from Item as i " +
            "where (lower(i.name) like lower(concat('%', ?1,'%')) " +
            "or lower(i.description) like lower(concat('%', ?1,'%'))) " +
            "and i.available = true")
    Page<Item> searchItem(String text, Pageable page);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    List<Item> findAllByRequestId(Long id);
}
