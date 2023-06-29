package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .available(item.isAvailable())
                .description(item.getDescription())
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }
}
