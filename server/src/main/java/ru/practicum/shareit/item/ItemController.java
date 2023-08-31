package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи " + itemDto + " от пользователя с id " + userId);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление отзыва от пользователя с id " + userId + " на вещь с id " + itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments getItem(@RequestHeader(USER_ID_HEADER) Long id,
                                                  @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с id " + itemId);
        return itemService.getItemDtoWithBookingsAndComments(id, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> getAllItemsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                  @RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "100") int size) {
        log.info("Запрос на получение всех вещей пользователя с id " + userId);
        return itemService.getAllItemsOfUser(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id " + itemId + " от пользователя с id " + ownerId);
        return itemService.updateItemById(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id " + itemId + " от пользователя с id " + ownerId);
        itemService.deleteItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "100") int size) {
        log.info("Поиск вещей по запросу: \"" + text + "\"");
        return itemService.getAllItemsBySearch(text, from, size);
    }
}
