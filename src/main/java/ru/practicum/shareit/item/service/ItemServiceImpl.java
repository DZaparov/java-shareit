package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BlankFieldException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }

        Item item = ItemMapper.toItem(itemDto);

        User owner = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + ownerId + " не найден."));
        item.setOwner(owner);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto, Long ownerId) {
        if (ownerId == null) {
            throw new BlankFieldException("Заголовок X-Sharer-User-Id не должен быть пустым");
        }
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + ownerId + " не найден."));

        Item itemToUpdate = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Вещь с идентификатором " + id + " не найдена."));
        Item item = ItemMapper.fillItem(itemDto, itemToUpdate);

        if (!ownerId.equals(itemToUpdate.getOwner().getId())) {
            throw new ForbiddenException("Вещь может менять только ее владелец");
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingDto getItemById(Long id, Long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Вещь с идентификатором " + id + " не найдена."));

        ItemWithBookingDto result;

        List<Booking> bookings = bookingRepository.findAllByItemId(id);

        BookingDto lastBooking = bookings
                .stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                .max(Booking::compareTo)
                .map(BookingMapper::toBookingDto)
                .orElse(null);

        BookingDto nextBooking = bookings
                .stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getItem().getOwner().getId().equals(ownerId))
                .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED))
                .min(Booking::compareTo)
                .map(BookingMapper::toBookingDto)
                .orElse(null);

        List<CommentDto> comments = commentRepository.findAllCommentByItemId(id)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        result = ItemMapper.toItemWithBookingDto(item, lastBooking, nextBooking, comments);

        return result;
    }

    @Override
    public List<ItemWithBookingDto> listItemsOfUser(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemWithBookingDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(getItemById(item.getId(), ownerId));
        }

        return result;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.searchItem(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Вещь с идентификатором " + itemId + " не найдена."));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с идентификатором " + userId + " не найден."));

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не может комментировать свою вещь");
        }

        List<Booking> bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(
                userId,
                LocalDateTime.now());

        bookings.stream()
                .findFirst()
                .orElseThrow(() -> new NotAvailableException("Пользователь не бронировал вещь"));

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
