package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws IOException {
        UserDto user = new UserDto(1L, "Vasia", "VasiaPupkin@gmail.com");
        ItemRequestDto itemRequest = new ItemRequestDto(
                1L,
                "MaK",
                user,
                LocalDateTime.now().plusHours(1).withNano(0));
        JsonContent<ItemRequestDto> result = json.write(itemRequest);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequest.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemRequest.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requester.id")
                .isEqualTo(itemRequest.getRequester().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.requester.name")
                .isEqualTo(itemRequest.getRequester().getName());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(itemRequest.getCreated().toString());
    }
}