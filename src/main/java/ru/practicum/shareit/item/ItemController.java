package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(userIdHeader) Long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи " + itemDto + " от пользователя с id " + userId);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userIdHeader) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление отзыва от пользователя с id " + userId + " на вещь с id " + itemId);
        return itemService.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingsAndComments getItem(@RequestHeader(userIdHeader) Long id,
            @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с id " + itemId);
        return itemService.getItemDtoWithBookingsAndComments(id, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingsAndComments> getAllItemsByUser(@RequestHeader(userIdHeader) Long userId) {
        log.info("Запрос на получение всех вещей пользователя с id " + userId);
        return itemService.getAllItemsOfUser(userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(userIdHeader) Long ownerId,
            @PathVariable Long itemId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id " + itemId + " от пользователя с id " + ownerId);
        return itemService.updateItemById(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader(userIdHeader) Long ownerId,
            @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id " + itemId + " от пользователя с id " + ownerId);
        itemService.deleteItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Поиск вещей по запросу: \"" + text + "\"");
        return itemService.getAllItemsBySearch(text);
    }
}
