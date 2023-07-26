package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            UserMapper.toUserDto(itemRequest.getRequester()),
            itemRequest.getCreated()
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return new ItemRequest(
            itemRequestDto.getId(),
            itemRequestDto.getDescription(),
            user,
            itemRequestDto.getCreated() == null ? LocalDateTime.now() : itemRequestDto.getCreated()
        );
    }

    public static ItemRequestWithItemsDto itemRequestToDtoWithItems(ItemRequest itemRequest, List<ItemForRequestsDto> items) {
        return new ItemRequestWithItemsDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }
}
