package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@Builder
public class Item {
    private Long id;
    private String name;
    private Long owner;
    private String description;
    private Boolean available;
    private Long request;

    public Item update(ItemDto itemDto) {
        if (itemDto.getName() == null) {
            itemDto.setName(name);
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(description);
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(available);
        }
        return Item.builder()
                .id(id)
                .name(itemDto.getName())
                .owner(itemDto.getOwner())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest())
                .build();
    }
}