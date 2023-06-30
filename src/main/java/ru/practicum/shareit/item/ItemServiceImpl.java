package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Service
@Slf4j
@Component("ItemServiceImpl")
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Override
    public Item createItem(ItemDto item, Integer ownerId) {
        return null;
    }

    @Override
    public Item updateItemById(Integer id, Integer userId) {
        return null;
    }

    @Override
    public Item getItemById(Integer id) {
        return null;
    }

    @Override
    public Collection<Item> getAllItemsOfUser(Integer ownerId) {
        return null;
    }

    @Override
    public Collection<Item> getAllItemsBySearch(String text, Integer userId) {
        return null;
    }
}
