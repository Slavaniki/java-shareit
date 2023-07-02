package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (userRepository.getUser(ownerId) != null) {
            Item item = ItemMapper.toItem(itemDto);
            Item newItem = itemRepository.createItem(item, ownerId);
            return ItemMapper.toItemDto(newItem);
        } else {
            throw new ResourceNotFoundException("Пользователя с id: " + ownerId + " не существует");
        }
    }

    @Override
    public ItemDto updateItemById(Long id, Long userId, ItemDto itemDto) {
        if (userRepository.getUser(userId) != null) {
            Collection<Item> itemById = new ArrayList<>();
            itemRepository.getAllItemsOfUser(userId).forEach(item -> {
                if (item.getId() == id) {
                    itemById.add(item);
                }
            });
            if (!itemById.isEmpty()) {
                Item item = itemRepository.getItemById(id);
                Item newItem = item.update(itemDto);
                return ItemMapper.toItemDto(itemRepository.updateItemById(id, newItem));
            } else {
                throw new ResourceNotFoundException("Пользователь с id: " + userId + " не является владельцем вещи");
            }
        } else {
            throw new ResourceNotFoundException("Пользователя с id: " + userId + " не существует");
        }
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(Long ownerId) {
        Collection<ItemDto> itemDtos = new ArrayList<>();
        Collection<Item> items = itemRepository.getAllItemsOfUser(ownerId);
        items.forEach(item -> itemDtos.add(ItemMapper.toItemDto(item)));
        return itemDtos;
    }

    @Override
    public Collection<ItemDto> getAllItemsBySearch(String text, Long userId) {
        Collection<ItemDto> itemDtos = new ArrayList<>();
        Collection<Item> items = itemRepository.getAllItemsBySearch(text, userId);
        items.forEach(item -> itemDtos.add(ItemMapper.toItemDto(item)));
        return itemDtos;
    }
}
