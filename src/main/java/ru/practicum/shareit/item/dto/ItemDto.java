package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private User owner;
    private String description;
    private boolean available;
    private Integer request;
}