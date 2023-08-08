package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws IOException {
        CommentDto comment = new CommentDto(1L, "Помогите мне, я пишу тесты уже очень долго", 1L,
                1L, "Славик", LocalDateTime.now().plusHours(1).withNano(0));
        JsonContent<CommentDto> result = json.write(comment);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(comment.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(comment.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(comment.getAuthorId().intValue());
        assertThat(result).extractingJsonPathValue("$.authorName").isEqualTo(comment.getAuthorName());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(comment.getCreated().toString());
    }
}