package ru.practicum.shareit.booking.service;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.dto.BookingDtoIncome;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public final class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getItem(),
            booking.getBooker(),
            booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoIncome bookingDtoIncome, Item item, User user) {
        return new Booking(
                null,
                bookingDtoIncome.getStart(),
                bookingDtoIncome.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public static BookingDtoWithBookerId toBookingDtoWithBookerID(Booking booking) {
        return new BookingDtoWithBookerId(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.itemToDto(booking.getItem()),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }
}
