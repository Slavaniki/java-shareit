package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoIncomeTest {
    @Autowired
    private JacksonTester<BookingDtoIncome> json;

    @Test
    void testBookingDtoIncome() throws IOException {
        BookingDtoIncome bookingDto = new BookingDtoIncome(LocalDateTime.now().plusHours(1).withNano(0),
                LocalDateTime.now().plusHours(2).withNano(0), 1L);
        JsonContent<BookingDtoIncome> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(bookingDto.getItemId().intValue());
    }
}

