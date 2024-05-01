package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByEndDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime before, LocalDateTime after);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByEndDesc(Long bookerId, BookingStatus state);

    List<Booking> findByItemOwnerIdOrderByEndDesc(Long bookerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId, LocalDateTime before, LocalDateTime after);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByEndDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByEndDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByEndDesc(Long bookerId, BookingStatus state);

    List<Booking> findAllByItemId(Long id);
}
