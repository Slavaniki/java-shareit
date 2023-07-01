package ru.practicum.shareit.item;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    HashMap<Long, Item> items = new HashMap<>();
    private final Map<Long, Collection<Long>> ownersItems = new HashMap<>();
    Long id = 1L;

    @Override
    public Item createItem(Item item, Long ownerId) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Описание не может быть пустым");
        }
        Long itemId = id++;
        item.setId(itemId);
        items.put(itemId, item);
        Collection<Long> ownerItems = new ArrayList<>();
        if (ownersItems.containsKey(ownerId)) {
            ownerItems = ownersItems.get(ownerId);
        }
        ownerItems.add(itemId);
        ownersItems.put(ownerId, ownerItems);
        log.info("Добавление вещи {}", item);
        return item;
    }

    @Override
    public Item updateItemById(Long id, Item item) {
        if (items.containsKey(id)) {
            items.replace(id, item);
            log.info("Обновление вещи {}", item);
        } else {
            throw new ResourceNotFoundException("Вещи с id: " + id + " не существует");
        }
        return item;
    }

    @Override
    public Item getItemById(Long id) {
        if (items.containsKey(id)) {
            log.info("Получение вещи с id: {}", id);
            return items.get(id);
        } else {
            throw new ResourceNotFoundException("Вещи с id: " + id + " не существует");
        }
    }

    @Override
    public Collection<Item> getAllItemsOfUser(Long ownerId) {
        return null;
    }

    @Override
    public Collection<Item> getAllItemsBySearch(String text, Long userId) {
        return null;
    }
}
