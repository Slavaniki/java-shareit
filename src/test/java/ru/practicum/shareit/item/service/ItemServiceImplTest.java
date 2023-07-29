package ru.practicum.shareit.item.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(6L, 7L));
    private final User kirchick = new User(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final UserDto kirchickDto = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final User yan = new User(2L, "Yan", "Yan@gmail.com");
    private final UserDto yanDto = new UserDto(2L, "Yan", "Yan@gmail.com");
    private final ItemRequest itemRequest = new ItemRequest(
            4L,
            "MaK",
            yan,
            LocalDateTime.of(2022, 10, 24, 12, 30, 0)
    );
    private final Item item = new Item(
            3L,
            "mouse",
            "cool",
            true,
            yan,
            itemRequest
    );
    private final ItemDto itemDto = new ItemDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            yanDto,
            item.getRequest().getId() == null ? null : item.getRequest().getId()
    );
    private final Booking lastBooking = new Booking(
            6L,
            LocalDateTime.of(2022, 10, 20, 12, 30),
            LocalDateTime.of(2022, 10, 21, 13, 35),
            item,
            kirchick,
            BookingStatus.APPROVED
    );
    private final Booking nextBooking = new Booking(
            7L,
            LocalDateTime.of(2022, 10, 23, 12, 35),
            LocalDateTime.of(2022, 10, 24, 13, 0),
            item,
            kirchick,
            BookingStatus.APPROVED
    );
    private final ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = new ItemDtoWithBookingsAndComments(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            yanDto,
            item.getRequest().getId() == null ? null : item.getRequest().getId(),
            new ArrayList<>(),
            BookingMapper.toBookingDtoWithBookerID(lastBooking),
            BookingMapper.toBookingDtoWithBookerID(nextBooking)
    );
    private final Comment comment = new Comment(
            8L,
            "awesome",
            item,
            kirchick,
            LocalDateTime.of(2022, 10, 25, 18, 1)
    );
    private final CommentDto commentDto = new CommentDto(
            comment.getId(),
            comment.getText(),
            comment.getItem().getId(),
            comment.getAuthor().getId(),
            comment.getAuthor().getName(),
            comment.getCreated()
    );
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemTest() {
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto result = itemService.createItem(kirchick.getId(), itemDto);
        assertEquals(itemDto, result);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.createItem(1000L, itemDto));
        Mockito
                .when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.createItem(kirchick.getId(), itemDto));
    }

    @Test
    void getItemByIdTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        ItemDto result = itemService.getItemById(item.getId());
        assertEquals(itemDto, result);
        Mockito
                .when(itemRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemById(1000L));
    }

    @Test
    void getItemDtoWithBookingsAndCommentsTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.existsById(yan.getId()))
                .thenReturn(true);
        Mockito
                .when(bookingRepository.getPastOrCurrentBookingByItemId(item.getId()))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(bookingRepository.getFutureBookingByItemId(item.getId()))
                .thenReturn(Optional.of(nextBooking));
        ItemDtoWithBookingsAndComments result = itemService.getItemDtoWithBookingsAndComments(
                yan.getId(), item.getId());
        assertEquals(itemDtoWithBookingsAndComments, result);
        Mockito
                .when(itemRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.getItemDtoWithBookingsAndComments(yan.getId(), 1000L));
    }

    @Test
    void GetItemDtoWithBookingsAndComments_UserIsOwnerTest() {
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                yanDto,
                item.getRequest().getId(),
                new ArrayList<>(),
                new BookingDtoWithBookerId(
                        6L,
                        LocalDateTime.of(2022, 10, 20, 12, 30),
                        LocalDateTime.of(2022, 10, 21, 13, 35),
                        itemDto,
                        kirchick.getId(),
                        BookingStatus.APPROVED
                ),new BookingDtoWithBookerId(
                7L,
                LocalDateTime.of(2022, 10, 23, 12, 35),
                LocalDateTime.of(2022, 10, 24, 13, 0),
                itemDto,
                kirchick.getId(),
                BookingStatus.APPROVED)
        );
        Mockito
                .when(userRepository.existsById(yanDto.getId()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.getPastOrCurrentBookingByItemId(itemDto.getId()))
                .thenReturn(Optional.of(lastBooking));
        Mockito
                .when(bookingRepository.getFutureBookingByItemId(itemDto.getId()))
                .thenReturn(Optional.of(nextBooking));
        Mockito
                .when(commentRepository.findCommentsByItem_Id(itemDto.getId()))
                .thenReturn(Collections.emptyList());
        ItemDtoWithBookingsAndComments result = itemService.getItemDtoWithBookingsAndComments(
                yan.getId(),
                itemDto.getId()
        );
        assertEquals(expected, result);
    }

    @Test
    void GetItemDtoWithBookingsAndComments_UserIsNotOwnerTest() {
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                yanDto,
                item.getRequest().getId(),
                new ArrayList<>(),
                null,
                null
        );
        Mockito
                .when(userRepository.existsById(kirchickDto.getId()))
                .thenReturn(true);
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(commentRepository.findCommentsByItem_Id(item.getId()))
                .thenReturn(Collections.emptyList());
        ItemDtoWithBookingsAndComments result = itemService.getItemDtoWithBookingsAndComments(
                kirchick.getId(),
                item.getId()
        );
        assertEquals(expected, result);
    }

    @Test
    void getAllItemsOfUserTest() {
        Mockito
                .when(itemRepository.findByOwnerIdOrderById(yan.getId(), PageRequest.of(0, 1)))
                .thenReturn(List.of(item));
        Mockito.lenient()
                .when(bookingRepository.getBookingsByItem_IdInOrderByEndAsc(itemIds))
                .thenReturn(List.of(lastBooking, nextBooking));
        ItemDtoWithBookingsAndComments expected = new ItemDtoWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                yanDto,
                item.getRequest().getId(),
                new ArrayList<>(),
                null,
                null
        );
        List<ItemDtoWithBookingsAndComments> result = itemService.getAllItemsOfUser(yan.getId(), 0, 1);
        assertEquals(List.of(expected), result);
    }

    @Test
    void updateItemByIdTest() {
        Item knife = new Item(5L,
                "нож",
                "острый",
                true,
                kirchick,
                null
        );
        ItemDto update = new ItemDto(knife.getId(),
                "большой нож",
                "затупился об банку тушёнки",
                false,
                kirchickDto,
                null);
        ItemDto expected = new ItemDto(
                knife.getId(),
                update.getName(),
                update.getDescription(),
                update.getAvailable(),
                update.getOwner(),
                null
        );
        Mockito
                .when(itemRepository.findById(knife.getId()))
                .thenReturn(Optional.of(knife));
        Mockito
                .when(itemRepository.save(knife))
                .thenReturn(knife);
        ItemDto result = itemService.updateItemById(kirchick.getId(), knife.getId(), update);
        assertEquals(expected, result);
    }

    @Test
    void UpdateItemById_withNullsTest() {
        Item knife = new Item(5L,
                "нож",
                "острый",
                true,
                kirchick,
                null
        );
        ItemDto updateItem = new ItemDto(knife.getId(),
                null,
                null,
                null,
                kirchickDto,
                null);
        ItemDto expected = new ItemDto(
                knife.getId(),
                knife.getName(),
                knife.getDescription(),
                knife.isAvailable(),
                updateItem.getOwner(),
                null
        );
        Mockito
                .when(itemRepository.findById(knife.getId()))
                .thenReturn(Optional.of(knife));
        Mockito
                .when(itemRepository.save(knife))
                .thenReturn(knife);
        ItemDto result = itemService.updateItemById(kirchick.getId(), knife.getId(), updateItem);
        assertEquals(expected, result);
    }

    @Test
    void updateItemById_UserIsNotOwnerTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItemById(
                        kirchick.getId(),
                        item.getId(),
                        new ItemDto(yan.getId(), null, null, null, null, null))
        );
    }

    @Test
    void updateItemById_ItemNotExistTest() {
        Mockito
                .when(itemRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.updateItemById(
                        yan.getId(),
                        1000L,
                        new ItemDto(yan.getId(), null, null, null, null, null))
        );
    }

    @Test
    void deleteItemByIdTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        itemService.deleteItemById(yan.getId(), item.getId());
        Mockito
                .verify(itemRepository, Mockito.times(1))
                .deleteById(item.getId());
        Mockito
                .when(itemRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemById(yan.getId(), 1000L));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        assertThrows(ResourceNotFoundException.class, () -> itemService.deleteItemById(kirchick.getId(), item.getId()));
    }

    @Test
    void getAllItemsBySearchTest() {
        Mockito
                .when(itemRepository.searchItems("mouse", PageRequest.of(0, 1)))
                .thenReturn(List.of(item));
        List<ItemDto> result = itemService.getAllItemsBySearch("mouse", 0, 1);
        assertEquals(List.of(itemDto), result);
        List<ItemDto> result2 = itemService.getAllItemsBySearch("", 0, 1);
        assertEquals(Collections.emptyList(), result2);
    }

    @Test
    void createCommentTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(bookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                                eq(item.getId()),
                                eq(kirchick.getId()),
                                any(LocalDateTime.class)))
                .thenReturn(List.of(lastBooking));
        Mockito.when(commentRepository.save(any())).thenReturn(comment);
        CommentDto realComment = itemService.createComment(kirchick.getId(), item.getId(), commentDto);
        assertEquals(commentDto, realComment);
        Mockito
                .when(itemRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.createComment(
                kirchick.getId(),
                1000L,
                commentDto)
        );
    }

    @Test
    void createComment_UserNotExistTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemService.createComment(
                        1000L,
                        item.getId(),
                        commentDto)
        );
    }

    @Test
    void createComment_UserIsNotBookerTest() {
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(bookingRepository.findBookingsByItem_IdAndBooker_IdAndEndIsBefore(
                                eq(item.getId()),
                                eq(kirchick.getId()),
                                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        assertThrows(CommentFromUserWithoutBookingException.class, () -> itemService.createComment(kirchick.getId(), item.getId(), commentDto));
    }
}