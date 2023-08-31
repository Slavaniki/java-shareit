package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                    @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса " + itemRequestDto + " от пользователя с id " + userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Запрос на получение запроса с id " + requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение всех запросов пользователя с id " + userId);
        return itemRequestClient.getAllItemsRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherItemRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение всех запросов,созданных другими пользователями, от пользователя с id " + userId);
        return itemRequestClient.getAllOtherItemRequests(userId, from, size);
    }
}
