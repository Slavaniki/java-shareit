package ru.practicum.shareit.booking.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoWithBookerIdTest {
    @Autowired
    private JacksonTester<BookingDtoWithBookerId> json;

    @Test
    void testBookingDtoWithBookerId() throws IOException {
        UserDto kirchick = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
        ItemDto item = new ItemDto(2L,
                "mouse",
                "cool",
                true,
                kirchick,
                2L
        );
        BookingDtoWithBookerId bookingDto = new BookingDtoWithBookerId(1L, LocalDateTime.now().plusHours(1).withNano(0),
                LocalDateTime.now().plusHours(2).withNano(0), item, kirchick.getId(), BookingStatus.WAITING);
        JsonContent<BookingDtoWithBookerId> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathValue("$.item.description")
                .isEqualTo(bookingDto.getItem().getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDto.getBookerId().intValue());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}