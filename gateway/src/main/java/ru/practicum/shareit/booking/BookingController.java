package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long id,
                                         @Valid @RequestBody BookingDtoIncome bookingDtoIncome) {
        validateStartEndOfBooking(bookingDtoIncome);
        log.info("Запрос на добавление бронирования " + bookingDtoIncome + " от пользователя с id " + id);
        return bookingClient.createItem(id, bookingDtoIncome);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(USER_ID_HEADER) long id,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        log.info("Запрос на смену статуса: " + approved + " от пользователя с id " + id + " для бронирования с id " + bookingId);
        return bookingClient.updateStatus(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_ID_HEADER) long id,
                                 @PathVariable long bookingId) {
        return bookingClient.getById(id, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(USER_ID_HEADER) long id,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "100") @Positive int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getUserBookings(id, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) long id,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "100") @Positive int size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getOwnerBookings(id, state, from, size);
    }

    private void validateStartEndOfBooking(BookingDtoIncome bookingDtoIncome) {
        if (bookingDtoIncome.getStart().isAfter(bookingDtoIncome.getEnd())
                || bookingDtoIncome.getStart().isEqual(bookingDtoIncome.getEnd())) {
            throw new IllegalArgumentException("Начало бронирование не может быть равно или позже окончания бронирования");
        }
    }
}
