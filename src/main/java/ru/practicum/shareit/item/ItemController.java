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
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto item, @RequestHeader(userIdHeader) Long ownerId) {
        log.info("Добавление новой вещи");
        return itemService.createItem(item, ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItemById(@PathVariable Long id, @RequestHeader(userIdHeader) Long userId, @RequestBody ItemDto item) {
        log.info("Обновление вещи по id:" + id);
        return itemService.updateItemById(id, userId, item);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        log.info("Получение вещи по id:" + id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsOfUser(@RequestHeader(userIdHeader) Long ownerId) {
        log.info("Получение всех вещей пользователя по id: " + ownerId);
        return itemService.getAllItemsOfUser(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getAllItemsBySearch(@RequestParam("text") String text, @RequestHeader(userIdHeader) Long userId) {
        log.info("Поиск всех вещей по запросу: \"" + text + "\"");
        return itemService.getAllItemsBySearch(text, userId);
    }
}