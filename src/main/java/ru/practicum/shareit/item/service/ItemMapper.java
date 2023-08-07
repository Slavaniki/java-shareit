package ru.practicum.shareit.item.service;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public final class  ItemMapper {

    public static ItemDto itemToDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                item.getRequest() == null ? null : item.getRequest().getId()
        );
    }

    public static Item toItem(ItemDto itemDto, UserDto owner, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                UserMapper.toUser(owner),
                itemRequest);
    }

    public static ItemDtoWithBookingsAndComments toItemDtoWithBookingsAndComments(Item item,
                                                                                  List<CommentDto> comments,
                                                                                  BookingDtoWithBookerId lastBooking,
                                                                                  BookingDtoWithBookerId nextBooking) {
        return new ItemDtoWithBookingsAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                item.getRequest() == null ? null : item.getRequest().getId(),
                comments,
                lastBooking,
                nextBooking
        );
    }

    public static ItemForRequestsDto toItemDtoForRequests(final Item item) {
        return new ItemForRequestsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest().getId()
        );
    }

    public static List<ItemForRequestsDto> toItemForRequestsDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDtoForRequests).collect(Collectors.toList());
    }
}
