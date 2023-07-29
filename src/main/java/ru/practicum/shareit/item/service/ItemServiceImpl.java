package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exception.CommentFromUserWithoutBookingException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        final UserDto owner = UserMapper.toUserDto(
                userRepository.findById(userId).orElseThrow(() ->
                        new ResourceNotFoundException("Пользователя с id: " + userId + " не существует")));
        final Long itemRequestId = itemDto.getRequestId();
        ItemRequest request = null;
        if (itemRequestId != null) {
            request = requestRepository.findById(itemRequestId).orElseThrow(() ->
                    new ResourceNotFoundException("Запроса с id: " + itemRequestId + " не существует"));
        }
        final Item item = itemRepository.save(
                ItemMapper.toItem(itemDto, owner, request)
        );
        log.info("Вещь с id " + item.getId() + " успешно создана пользователем с id " + item.getOwner().getId());
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Вещи с id: " + itemId + " не существует"));
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDtoWithBookingsAndComments getItemDtoWithBookingsAndComments(Long userId, Long itemId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователя с id: " + userId + " не существует");
        }
        final Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Вещи с id: " + itemId + " не существует"));
        BookingDtoWithBookerId currentOrPastBooking = null;
        BookingDtoWithBookerId futureBooking = null;
        if (item.getOwner().getId().equals(userId)) {
            currentOrPastBooking = bookingRepository.getPastOrCurrentBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoWithBookerID)
                    .orElse(null);
            futureBooking = bookingRepository.getFutureBookingByItemId(item.getId())
                    .map(BookingMapper::toBookingDtoWithBookerID)
                    .orElse(null);
        }
        List<CommentDto> comments = commentRepository.findCommentsByItem_Id(item.getId())
                .stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDtoWithBookingsAndComments(
                item,
                comments,
                currentOrPastBooking,
                futureBooking
        );
    }

    @Override
    public List<ItemDtoWithBookingsAndComments> getAllItemsOfUser(Long userId, int from, int size) {
        List<ItemDtoWithBookingsAndComments> resultList = new ArrayList<>();
        List<Item> items = itemRepository.findByOwnerIdOrderById(userId, PageRequest.of(from / size, size));
        Set<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toSet());
        List<Comment> comments = commentRepository.findCommentsByItem_IdIn(itemIds);
        List<Booking> bookings = bookingRepository.getBookingsByItem_IdInOrderByEndAsc(itemIds);
        for (Item item : items) {
            List<CommentDto> commentDtos = new ArrayList<>();
            for (Comment comment : comments) {
                if (comment.getItem().equals(item)) {
                    commentDtos.add(CommentMapper.toCommentDto(comment));
                }
            }
            List<Booking> pastOrCurrentBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item))
                            .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                            .limit(1)
                            .collect(Collectors.toList());
            BookingDtoWithBookerId lastBooking = null;
            if (pastOrCurrentBooking.size() != 0) {
                lastBooking = BookingMapper.toBookingDtoWithBookerID(pastOrCurrentBooking.get(0));
            }
            List<Booking> futureBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item))
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .limit(1)
                            .collect(Collectors.toList());
            BookingDtoWithBookerId nextBooking = null;
            if (futureBooking.size() != 0) {
                nextBooking = BookingMapper.toBookingDtoWithBookerID(futureBooking.get(0));
            }
            resultList.add(
                    ItemMapper.toItemDtoWithBookingsAndComments(
                            item,
                            commentDtos,
                            lastBooking,
                            nextBooking
            ));
        }
        return resultList;
    }

    @Override
    @Transactional
    public ItemDto updateItemById(Long ownerId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Вещи с id: " + itemId + " не существует"));
        if (!ownerId.equals(item.getOwner().getId())) {
            throw new ResourceNotFoundException("Пользователь с id: " + ownerId + " не является владельцем вещи с id " + itemId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        log.info("Вещь с id " + updatedItem.getId() + " успешно обновлена пользователем с id " + updatedItem.getOwner().getId());
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteItemById(Long ownerId, Long itemId) {
        final ItemDto itemDto = getItemById(itemId);
        if (ownerId.equals(itemDto.getOwner().getId())) {
            itemRepository.deleteById(itemId);
            log.info("Вещь с id " + itemId + " успешно удалена пользователем с id " + ownerId);
        } else {
            throw new ResourceNotFoundException("Пользователь с id: " + ownerId + " не является владельцем вещи с id " + itemId);
        }
    }

    @Override
    public List<ItemDto> getAllItemsBySearch(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchItems(text, PageRequest.of(from / size, size))
                .stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResourceNotFoundException("Вещи с id: " + itemId + " не существует"));
        final User author = userRepository.findById(authorId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id: " + authorId + " не существует"));
        final List<Booking> bookings = bookingRepository
                .findBookingsByItem_IdAndBooker_IdAndEndIsBefore(itemId, authorId, LocalDateTime.now());
        if (bookings.stream().findAny().isEmpty()) {
            throw new CommentFromUserWithoutBookingException("У пользователя с id " + authorId + " нет бронирований и прав добавлять отзывы");
        }
        final Comment comment = CommentMapper.toComment(commentDto, item, author);
        commentRepository.save(comment);
        log.info("Отзыв пользователя с id " + authorId + " успешно добавлен");
        return CommentMapper.toCommentDto(comment);
    }
}
