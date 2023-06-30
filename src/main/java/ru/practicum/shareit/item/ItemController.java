package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item createItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Добавление новой вещи");
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItemById(@PathVariable Integer id, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Обновление вещи по id:" + id);
        return itemService.updateItemById(id, userId);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable Integer id) {
        log.info("Получение вещи по id:" + id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public Collection<Item> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Получение всех вещей пользователя по id: " + ownerId);
        return itemService.getAllItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public Collection<Item> getAllItemsBySearch(@RequestParam("text") String text, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Поиск всех вещей по запросу: \"" + text + "\"");
        return itemService.getAllItemsBySearch(text, userId);
    }
}