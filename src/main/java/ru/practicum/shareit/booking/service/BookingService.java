package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingDtoIncome bookingDtoIncome);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingState state);

    BookingDto updateBookingStatus(long userId, long bookingId, boolean updateStatus);

    List<BookingDto> getOwnerBookings(long ownerId, BookingState state);
}
