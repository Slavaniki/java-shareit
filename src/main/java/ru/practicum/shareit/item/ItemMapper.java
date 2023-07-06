package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

public final class ItemMapper {
    private ItemMapper() {
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .available(item.getAvailable())
                .description(item.getDescription())
                .request(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .owner(itemDto.getOwner())
                .available(itemDto.getAvailable())
                .description(itemDto.getDescription())
                .request(itemDto.getRequest())
                .build();
    }
}
