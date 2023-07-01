package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
        if (userRepository.getUser(userId) != null && userId.equals(itemDto.getOwner())) {
            Item item = itemRepository.getItemById(id);
            Item newItem = item.update(itemDto);
            return ItemMapper.toItemDto(itemRepository.updateItemById(id, newItem));
        } else {
            throw new ResourceNotFoundException("Пользователя с id: " + userId + " не существует или он не является владельцем вещи");
        }
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(id));
    }

    @Override
    public Collection<ItemDto> getAllItemsOfUser(Long ownerId) {
        return null;
    }

    @Override
    public Collection<ItemDto> getAllItemsBySearch(String text, Long userId) {
        return null;
    }
}
