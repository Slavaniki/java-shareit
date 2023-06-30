package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    public Item createItem(ItemDto item, Integer ownerId);

    public Item updateItemById(Integer id, Integer userId);

    public Item getItemById(Integer id);

    public Collection<Item> getAllItemsOfUser(Integer ownerId);

    public Collection<Item> getAllItemsBySearch(String text, Integer userId);
}
