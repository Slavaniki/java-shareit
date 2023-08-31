package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws IOException {
        UserDto kirchick = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
        ItemDto itemDto = new ItemDto(1L, "mouse", "cool", true, kirchick, 1L);
        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathValue("$.owner.id").isEqualTo(itemDto.getOwner().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo(itemDto.getOwner().getName());
        assertThat(result).extractingJsonPathValue("$.owner.email").isEqualTo(itemDto.getOwner().getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(
                itemDto.getRequestId().intValue());
    }
}