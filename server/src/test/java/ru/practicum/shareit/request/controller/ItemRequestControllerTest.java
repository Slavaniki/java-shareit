package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    private final UserDto kirchick = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final UserDto yan = new UserDto(2L, "Yan", "Yan@gmail.com");
    private final ItemRequestDto itemRequest = new ItemRequestDto(
            3L,
            "MaK",
            kirchick,
            LocalDateTime.of(2022, 10, 20, 15, 55, 0)
    );
    private final ItemDto item = new ItemDto(
            4L,
            "mouse",
            "cool",
            true,
            yan,
            itemRequest.getId()
    );
    private final ItemForRequestsDto itemForRequests = new ItemForRequestsDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getAvailable(),
            item.getRequestId()
    );
    private final ItemRequestWithItemsDto itemRequestWithItems = new ItemRequestWithItemsDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            List.of(itemForRequests)
    );
    private final String requestCreated = "2022-10-20T15:55:00";
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequestTest() throws Exception {
        Mockito
                .when(itemRequestService.createItemRequest(anyLong(), any()))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .content(mapper.writeValueAsString(itemRequest))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequest.getRequester().getId()), Long.class))
                .andExpect(jsonPath("$.requester.name", is(itemRequest.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(itemRequest.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(requestCreated)));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .createItemRequest(1L, itemRequest);
    }

    @Test
    void getAllItemsRequestsByUserTest() throws Exception {
        Mockito
                .when(itemRequestService.getAllItemsRequestsByUser(anyLong()))
                .thenReturn(List.of(itemRequestWithItems));
        mvc.perform(get("/requests")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestCreated)))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(itemRequestWithItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(itemRequestWithItems.getItems().get(0).getName())))
                .andExpect(
                        jsonPath("$.[0].items[0].description",
                                is(itemRequestWithItems.getItems().get(0).getDescription()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].available",
                                is(itemRequestWithItems.getItems().get(0).isAvailable()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].requestId",
                                is(itemRequestWithItems.getItems().get(0).getRequestId()), Long.class
                        )
                );
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllItemsRequestsByUser(1L);
    }

    @Test
    void getAllOtherItemRequestsTest() throws Exception {
        Mockito
                .when(itemRequestService.getAllOtherItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestWithItems));
        mvc.perform(get("/requests/all")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemRequestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestWithItems.getDescription())))
                .andExpect(jsonPath("$.[0].created", is(requestCreated)))
                .andExpect(jsonPath("$.[0].items[0].id",
                        is(itemRequestWithItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].items[0].name",
                        is(itemRequestWithItems.getItems().get(0).getName())))
                .andExpect(
                        jsonPath("$.[0].items[0].description",
                                is(itemRequestWithItems.getItems().get(0).getDescription()))
                )
                .andExpect(
                        jsonPath("$.[0].items[0].available",
                                is(itemRequestWithItems.getItems().get(0).isAvailable()))
                )
                .andExpect(
                        jsonPath(
                                "$.[0].items[0].requestId",
                                is(itemRequestWithItems.getItems().get(0).getRequestId()), Long.class
                        )
                );
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllOtherItemRequests(1L, 0, 1);
    }

    @Test
    void getItemRequestTest() throws Exception {
        Mockito
                .when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestWithItems);
        mvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header("X-Sharer-User-Id", kirchick.getId())
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())))
                .andExpect(jsonPath("$.items[0].id",
                        is(itemRequestWithItems.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name",
                        is(itemRequestWithItems.getItems().get(0).getName())))
                .andExpect(
                        jsonPath("$.items[0].description",
                                is(itemRequestWithItems.getItems().get(0).getDescription()))
                )
                .andExpect(
                        jsonPath("$.items[0].available",
                                is(itemRequestWithItems.getItems().get(0).isAvailable()))
                )
                .andExpect(
                        jsonPath(
                                "$.items[0].requestId",
                                is(itemRequestWithItems.getItems().get(0).getRequestId()), Long.class
                        ))
                .andExpect(jsonPath("$.created", is(requestCreated)));
        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequest(1L, itemRequest.getId());
    }
}
