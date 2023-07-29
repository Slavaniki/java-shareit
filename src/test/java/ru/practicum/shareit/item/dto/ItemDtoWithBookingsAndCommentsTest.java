package ru.practicum.shareit.item.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoWithBookingsAndCommentsTest {
    @Autowired
    private JacksonTester<ItemDtoWithBookingsAndComments> jacksonTester;

    @Test
    void testItemDtoWithBookingsAndComments() throws IOException {
        User kirchick = new User(1L, "9impulse", "coolFireBeaver@gmail.com");
        UserDto kirchickDto = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
        Item item = new Item(2L, "mouse", "cool", true, kirchick, null);
        ItemDto itemDto = new ItemDto(2L, "mouse", "cool", true, kirchickDto, 2L);
        BookingDtoWithBookerId lastBooking = new BookingDtoWithBookerId(
                6L,
                LocalDateTime.of(2022, 10, 20, 12, 30),
                LocalDateTime.of(2022, 10, 21, 13, 35),
                itemDto,
                kirchickDto.getId(),
                BookingStatus.APPROVED
        );
        BookingDtoWithBookerId nextBooking = new BookingDtoWithBookerId(
                7L,
                LocalDateTime.of(2022, 10, 23, 12, 35),
                LocalDateTime.of(2022, 10, 24, 13, 0),
                itemDto,
                kirchickDto.getId(),
                BookingStatus.APPROVED
        );
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = new ItemDtoWithBookingsAndComments(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                kirchickDto,
                itemDto.getRequestId(),
                Collections.emptyList(),
                lastBooking,
                nextBooking);
        JsonContent<ItemDtoWithBookingsAndComments> result = jacksonTester.write(itemDtoWithBookingsAndComments);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoWithBookingsAndComments.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDtoWithBookingsAndComments.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDtoWithBookingsAndComments.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(itemDtoWithBookingsAndComments.getAvailable());
        assertThat(result).extractingJsonPathValue("$.owner.id").isEqualTo(itemDtoWithBookingsAndComments.getOwner().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo(itemDtoWithBookingsAndComments.getOwner().getName());
        assertThat(result).extractingJsonPathValue("$.owner.email").isEqualTo(itemDtoWithBookingsAndComments.getOwner().getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(
                itemDtoWithBookingsAndComments.getRequestId().intValue());
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(Collections.emptyList());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.id").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.name").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(
                itemDtoWithBookingsAndComments.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.id").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.name").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(
                itemDtoWithBookingsAndComments.getLastBooking().getBookerId().intValue());
    }
}