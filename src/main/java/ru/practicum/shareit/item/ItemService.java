package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    public ItemDto createItem(ItemDto item, Long ownerId);

    public ItemDto updateItemById(Long id, Long userId, ItemDto item);

    public ItemDto getItemById(Long id);

    public Collection<ItemDto> getAllItemsOfUser(Long ownerId);

    public Collection<ItemDto> getAllItemsBySearch(String text, Long userId);
}
