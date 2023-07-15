package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(userIdHeader) long id,
                                 @Valid @RequestBody BookingDtoIncome bookingDtoIncome) {
        log.info("Запрос на добавление бронирования " + bookingDtoIncome + " от пользователя с id " + id);
        return bookingService.createBooking(id, bookingDtoIncome);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(userIdHeader) long id,
                                            @PathVariable long bookingId,
                                            @RequestParam boolean approved) {
        log.info("Запрос на смену статуса: " + approved + " от пользователя с id " + id + " для бронирования с id " + bookingId);
        return bookingService.updateBookingStatus(id, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(userIdHeader) long id,
                                 @PathVariable long bookingId) {
        return bookingService.getBookingById(id, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader(userIdHeader) long id,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getUserBookings(id, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader(userIdHeader) long id,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return bookingService.getOwnerBookings(id, state);
    }
}
