package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    User kirchick;
    User yan;
    Item item;
    BookingDto waitingBooking;
    BookingDto approvedBooking;
    BookingDtoIncome bookingDtoIncome;
    private final String startDate = "2023-10-20T12:30:00";
    private final String endDate = "2023-10-21T13:35:00";

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        kirchick = new User(1L, "9impulse", "coolFireBeaver@gmail.com");
        yan = new User(2L, "Yan", "Yan@gmail.com");
        item = new Item(3L,
                "mouse",
                "cool",
                true,
                yan,
                null
        );
        waitingBooking = new BookingDto(
                4L,
                LocalDateTime.of(2023, 10, 20, 12, 30),
                LocalDateTime.of(2023, 10, 21, 13, 35),
                item,
                kirchick,
                BookingStatus.WAITING
        );
        approvedBooking = new BookingDto(
                waitingBooking.getId(),
                waitingBooking.getStart(),
                waitingBooking.getEnd(),
                waitingBooking.getItem(),
                waitingBooking.getBooker(),
                BookingStatus.APPROVED
        );
        bookingDtoIncome = new BookingDtoIncome(
                waitingBooking.getStart(),
                waitingBooking.getEnd(),
                waitingBooking.getBooker().getId()
        );
    }

    @Test
    void createBookingTest() throws Exception {
        Mockito
                .when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(waitingBooking);
        mvc.perform(post("/bookings")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .content(mapper.writeValueAsString(bookingDtoIncome))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(waitingBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(waitingBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(waitingBooking.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(waitingBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(waitingBooking.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(waitingBooking.getStatus().name())));
        Mockito
                .verify(bookingService, Mockito.times(1))
                .createBooking(1L, bookingDtoIncome);
    }

    @Test
    void createBooking_withNotFoundTest() throws Exception {
        Mockito
                .when(bookingService.createBooking(anyLong(), any()))
                .thenThrow(new ResourceNotFoundException("Пользователя с id " + 1L + " не существует"));
        mvc.perform(post("/bookings")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .content(mapper.writeValueAsString(bookingDtoIncome))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingStatusTest() throws Exception {
        Mockito
                .when(bookingService.updateBookingStatus(anyLong(), anyLong(), eq(true)))
                .thenReturn(approvedBooking);
        mvc.perform(patch("/bookings/{bookingId}", waitingBooking.getId())
                                .header("X-Sharer-User-Id", yan.getId())
                                .param("approved", "true")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(approvedBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(approvedBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(approvedBooking.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(approvedBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(approvedBooking.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(approvedBooking.getStatus().name())));
        Mockito.verify(bookingService, Mockito.times(1))
                .updateBookingStatus(2L, 4L, true);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito
                .when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(waitingBooking);
        mvc.perform(get("/bookings/{bookingId}", waitingBooking.getId())
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(waitingBooking.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(startDate)))
                .andExpect(jsonPath("$.end", is(endDate)))
                .andExpect(jsonPath("$.item.id", is(waitingBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(waitingBooking.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(waitingBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(waitingBooking.getBooker().getName())))
                .andExpect(jsonPath("$.status", is(waitingBooking.getStatus().name())));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingById(1L, 4L);
    }

    @Test
    void getUserBookingsTest() throws Exception {
        Mockito
                .when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(waitingBooking));
        mvc.perform(get("/bookings")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(waitingBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(waitingBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(waitingBooking.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(waitingBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(waitingBooking.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(waitingBooking.getStatus().name())));
        Mockito.verify(bookingService, Mockito.times(1))
                .getUserBookings(1L, BookingState.ALL, 0, 1);
    }

    @Test
    void getOwnerBookingsTest() throws Exception {
        Mockito
                .when(bookingService.getOwnerBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(waitingBooking));
        mvc.perform(get("/bookings/owner")
                                .header("X-Sharer-User-Id", kirchick.getId())
                                .param("state", "ALL")
                                .param("from", "0")
                                .param("size", "1")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(waitingBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", is(startDate)))
                .andExpect(jsonPath("$.[0].end", is(endDate)))
                .andExpect(jsonPath("$.[0].item.id", is(waitingBooking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(waitingBooking.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(waitingBooking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.name", is(waitingBooking.getBooker().getName())))
                .andExpect(jsonPath("$.[0].status", is(waitingBooking.getStatus().name())));
    }
}