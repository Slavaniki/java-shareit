package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemForRequestsDtoTest {
    @Autowired
    private JacksonTester<ItemForRequestsDto> json;

    @Test
    void testItemForRequestsDto() throws IOException {
        ItemForRequestsDto item = new ItemForRequestsDto(1L, "Apple", "Great fruit", true, 2L);
        JsonContent<ItemForRequestsDto> result = json.write(item);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(item.isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(
                item.getRequestId().intValue());
    }
}
