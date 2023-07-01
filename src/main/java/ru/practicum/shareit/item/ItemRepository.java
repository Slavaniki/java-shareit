package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemRepository {
    public Item createItem(Item item, Long ownerId);

    public Item updateItemById(Long id, Item item);

    public Item getItemById(Long id);

    public Collection<Item> getAllItemsOfUser(Long ownerId);

    public Collection<Item> getAllItemsBySearch(String text, Long userId);
}
