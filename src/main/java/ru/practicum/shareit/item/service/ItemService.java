package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long id, ItemDto itemDto);

    ItemDto getItemById(Long id);

    ItemDtoWithBookingsAndComments getItemDtoWithBookingsAndComments(Long userId, Long itemId);

    List<ItemDtoWithBookingsAndComments> getAllItemsOfUser(Long userId);

    ItemDto updateItemById(Long ownerId, Long itemId, ItemDto itemDto);

    void deleteItemById(Long ownerId, Long itemId);

    List<ItemDto> getAllItemsBySearch(String text);

    CommentDto createComment(Long authorId, Long itemId, CommentDto commentDto);
}
