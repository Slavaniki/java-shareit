package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                              @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос на добавление запроса " + itemRequestDto + " от пользователя с id " + userId);
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @PathVariable Long requestId) {
        log.info("Запрос на получение запроса с id " + requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getAllItemsRequestsByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение всех запросов пользователя с id " + userId);
        return itemRequestService.getAllItemsRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllOtherItemRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение всех запросов,созданных другими пользователями, от пользователя с id " + userId);
        return itemRequestService.getAllOtherItemRequests(userId, from, size);
    }
}
