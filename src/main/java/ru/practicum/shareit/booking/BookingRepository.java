package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerIdOrderByEndDesc(Long bookerId, Pageable page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime before, LocalDateTime after, Pageable page);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime now, Pageable page);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime now);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByBookerIdAndStatusOrderByEndDesc(Long bookerId, BookingStatus state, Pageable page);

    Page<Booking> findByItemOwnerIdOrderByEndDesc(Long bookerId, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime before, LocalDateTime after, Pageable page);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByEndDesc(Long bookerId, LocalDateTime now, Pageable page);

    Page<Booking> findByItemOwnerIdAndStatusOrderByEndDesc(Long bookerId, BookingStatus state, Pageable page);

    List<Booking> findAllByItemId(Long id);
}
