package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на добавление вещи " + itemDto + " от пользователя с id " + userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление отзыва от пользователя с id " + userId + " на вещь с id " + itemId);
        return itemClient.createComment(userId, itemId, commentDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) Long id,
            @PathVariable Long itemId) {
        log.info("Запрос на получение вещи с id " + itemId);
        return itemClient.getItem(id, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                                  @RequestParam(defaultValue = "100") @Positive int size) {
        log.info("Запрос на получение всех вещей пользователя с id " + userId);
        return itemClient.getAllItemsOfUser(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи с id " + itemId + " от пользователя с id " + ownerId);
        return itemClient.updateItemById(ownerId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(USER_ID_HEADER) Long ownerId,
            @PathVariable Long itemId) {
        log.info("Запрос на удаление вещи с id " + itemId + " от пользователя с id " + ownerId);
        return itemClient.deleteItemById(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestParam String text,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "100") @Positive int size) {
        log.info("Поиск вещей по запросу: \"" + text + "\"");
        return itemClient.getAllItemsBySearch(text, userId, from, size);
    }
}
