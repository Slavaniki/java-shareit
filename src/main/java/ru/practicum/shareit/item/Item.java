package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {
    private Integer id;
    private String name;
    private User owner;
    private String description;
    private boolean available;
    private ItemRequest request;
}