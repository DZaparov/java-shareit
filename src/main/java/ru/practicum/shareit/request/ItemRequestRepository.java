package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.nio.channels.FileChannel;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> getItemRequestsByRequestorId(Long requestorId);

    Page<ItemRequest> findAllByRequestorIdNotLike(Long requestorId, Pageable page);
}
