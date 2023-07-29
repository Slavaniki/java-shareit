package ru.practicum.shareit.item.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private final UserDto kirchick = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final UserDto yan = new UserDto(2L, "Yan", "Yan@gmail.com");
    private final ItemDto item = new ItemDto(
            3L,
            "mouse",
            "cool",
            true,
            yan,
            1L
    );
    private final CommentDto comment = new CommentDto(
            4L,
            "awesome",
            item.getId(),
            kirchick.getId(),
            kirchick.getName(),
            LocalDateTime.of(2022, 10, 25, 18, 1)
    );
    private final BookingDtoWithBookerId lastBooking = new BookingDtoWithBookerId(
            5L,
            LocalDateTime.of(2022, 10, 20, 12, 30),
            LocalDateTime.of(2022, 10, 21, 13, 35),
            item,
            kirchick.getId(),
            BookingStatus.APPROVED
    );
    private final BookingDtoWithBookerId nextBooking = new BookingDtoWithBookerId(
            6L,
            LocalDateTime.of(2022, 10, 23, 12, 35),
            LocalDateTime.of(2022, 10, 24, 13, 0),
            item,
            kirchick.getId(),
            BookingStatus.APPROVED
    );
    private final ItemDtoWithBookingsAndComments itemWithCommentsAndBookings = new ItemDtoWithBookingsAndComments(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getOwner(),
            1L,
            List.of(comment),
            lastBooking,
            nextBooking
    );
    private final String commentCreated = "2022-10-25T18:01:00";
    private final String lastBookingStart = "2022-10-20T12:30:00";
    private final String lastBookingEnd = "2022-10-21T13:35:00";
    private final String nextBookingStart = "2022-10-23T12:35:00";
    private final String nextBookingEnd = "2022-10-24T13:00:00";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    void CreateItemTest() throws Exception {
        Mockito
                .when(itemService.createItem(anyLong(), any()))
                .thenReturn(item);
        mvc.perform(post("/items")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .content(mapper.writeValueAsString(item))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(item.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(item.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(item.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class));
        Mockito.verify(itemService, Mockito.times(1))
                .createItem(1L, item);
    }

    @Test
    void createCommentTest() throws Exception {
        Mockito.when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(comment);
        mvc.perform(post("/items/{itemId}/comment", item.getId())
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .content(mapper.writeValueAsString(comment))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.itemId", is(comment.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(comment.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentCreated)));
        Mockito.verify(itemService, Mockito.times(1))
                .createComment(1L, 3L, comment);
    }

    @Test
    void handleGetItem() throws Exception {
        Mockito
                .when(itemService.getItemDtoWithBookingsAndComments(anyLong(), anyLong()))
                .thenReturn(itemWithCommentsAndBookings);
        mvc.perform(get("/items/{itemId}", item.getId())
                                .header("X-Sharer-User-Id", yan.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.description", is(itemWithCommentsAndBookings.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithCommentsAndBookings.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(itemWithCommentsAndBookings
                        .getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(itemWithCommentsAndBookings
                        .getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(itemWithCommentsAndBookings
                        .getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(itemWithCommentsAndBookings
                        .getRequestId()), Long.class))
                .andExpect(
                        jsonPath("$.comments[0].id", is(itemWithCommentsAndBookings
                                .getComments().get(0).getId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.comments[0].text", is(itemWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].itemId",
                                is(itemWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].authorId",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.comments[0].authorName",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.comments[0].created", is(commentCreated)))
                .andExpect(
                        jsonPath("$.lastBooking.id", is(itemWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.lastBooking.start", is(lastBookingStart)))
                .andExpect(jsonPath("$.lastBooking.end", is(lastBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.lastBooking.item.id",
                                is(itemWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.lastBooking.bookerId",
                                is(itemWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.nextBooking.id", is(itemWithCommentsAndBookings
                                .getNextBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.nextBooking.start", is(nextBookingStart)))
                .andExpect(jsonPath("$.nextBooking.end", is(nextBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.nextBooking.item.id",
                                is(itemWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.nextBooking.bookerId",
                                is(itemWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );
        Mockito.verify(itemService, Mockito.times(1))
                .getItemDtoWithBookingsAndComments(2L, 3L);
    }

    @Test
    void getAllItemsOfUserTest() throws Exception {
        Mockito
                .when(itemService.getAllItemsOfUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemWithCommentsAndBookings));
        mvc.perform(get("/items")
                                .header("X-Sharer-User-Id", yan.getId())
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemWithCommentsAndBookings.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemWithCommentsAndBookings.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemWithCommentsAndBookings
                        .getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemWithCommentsAndBookings.getAvailable())))
                .andExpect(
                        jsonPath("$.[0].comments[0].id", is(itemWithCommentsAndBookings
                                        .getComments().get(0).getId()),
                                Long.class)
                )
                .andExpect(
                        jsonPath("$.[0].comments[0].text", is(itemWithCommentsAndBookings
                                .getComments().get(0).getText()))
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].itemId",
                                is(itemWithCommentsAndBookings.getComments().get(0).getItemId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].authorId",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].comments[0].authorName",
                                is(itemWithCommentsAndBookings.getComments().get(0).getAuthorName())
                        )
                )
                .andExpect(jsonPath("$.[0].comments[0].created", is(commentCreated)))
                .andExpect(
                        jsonPath("$.[0].lastBooking.id", is(itemWithCommentsAndBookings
                                .getLastBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.[0].lastBooking.start", is(lastBookingStart)))
                .andExpect(jsonPath("$.[0].lastBooking.end", is(lastBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.[0].lastBooking.item.id",
                                is(itemWithCommentsAndBookings.getLastBooking().getItem().getId()), Long.class
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.[0].lastBooking.bookerId",
                                is(itemWithCommentsAndBookings.getLastBooking().getBookerId()), Long.class)
                )
                .andExpect(
                        jsonPath("$.[0].nextBooking.id", is(itemWithCommentsAndBookings.getNextBooking().getId()), Long.class)
                )
                .andExpect(jsonPath("$.[0].nextBooking.start", is(nextBookingStart)))
                .andExpect(jsonPath("$.[0].nextBooking.end", is(nextBookingEnd)))
                .andExpect(
                        jsonPath(
                                "$.[0].nextBooking.item.id",
                                is(itemWithCommentsAndBookings.getNextBooking().getItem().getId()), Long.class)
                )
                .andExpect(
                        jsonPath(
                                "$.[0].nextBooking.bookerId",
                                is(itemWithCommentsAndBookings.getNextBooking().getBookerId()), Long.class
                        )
                );
        Mockito.verify(itemService, Mockito.times(1))
                .getAllItemsOfUser(2L, 0, 1);
    }

    @Test
    void updateItemByIdTest() throws Exception {
        Mockito
                .when(itemService.updateItemById(anyLong(), anyLong(), any()))
                .thenReturn(item);
        mvc.perform(patch("/items/{itemId}", item.getId())
                                .header("X-Sharer-User-Id", yan.getId())
                                .content(mapper.writeValueAsString(item))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.owner.id", is(item.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.owner.name", is(item.getOwner().getName())))
                .andExpect(jsonPath("$.owner.email", is(item.getOwner().getEmail())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId()), Long.class));
        Mockito.verify(itemService, Mockito.times(1))
                .updateItemById(2L, 3L, item);
    }

    @Test
    void deleteItemByIdTest() throws Exception {
        itemService.deleteItemById(anyLong(), anyLong());
        Mockito
                .verify(itemService, Mockito.times(1))
                .deleteItemById(anyLong(), anyLong());
        mvc.perform(delete("/items/{itemId}", item.getId())
                                .header("X-Sharer-User-Id", yan.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsBySearchTest() throws Exception {
        Mockito
                .when(itemService.getAllItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                                .param("text", "table")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(item.getName())))
                .andExpect(jsonPath("$.[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$.[0].owner.id", is(item.getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.[0].owner.name", is(item.getOwner().getName())))
                .andExpect(jsonPath("$.[0].owner.email", is(item.getOwner().getEmail())))
                .andExpect(jsonPath("$.[0].requestId", is(item.getRequestId()), Long.class));
        Mockito.verify(itemService, Mockito.times(1))
                .getAllItemsBySearch("table", 0, 1);
    }
}
