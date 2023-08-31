package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestWithItemsDto> getAllItemsRequestsByUser(Long userId);

    List<ItemRequestWithItemsDto> getAllOtherItemRequests(Long userId, int from, int size);

    ItemRequestWithItemsDto getItemRequest(Long userId, Long requestId);

    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);
}
