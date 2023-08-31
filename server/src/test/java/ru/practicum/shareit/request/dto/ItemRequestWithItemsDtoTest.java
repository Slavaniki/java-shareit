package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestWithItemsDtoTest {
    @Autowired
    private JacksonTester<ItemRequestWithItemsDto> json;

    @Test
    void testItemRequestWithItemsDto() throws IOException {
        List<ItemForRequestsDto> itemForRequestsDtos = new ArrayList<>();
        ItemRequestWithItemsDto itemRequest = new ItemRequestWithItemsDto(
                1L,
                "MaK",
                LocalDateTime.now().plusHours(1).withNano(0),
                itemForRequestsDtos);
        JsonContent<ItemRequestWithItemsDto> result = json.write(itemRequest);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequest.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.description")
                .isEqualTo(itemRequest.getDescription());
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(itemRequest.getCreated().toString());
        assertThat(result).extractingJsonPathValue("$.items")
                .isEqualTo(itemRequest.getItems());;
    }
}