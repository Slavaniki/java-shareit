package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
