package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    @Override
    public List<ItemRequestWithItemsDto> getAllItemsRequestsByUser(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Пользователя с id " + userId + " не существует");
        }
        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return getRequestDtoWithItems(requests);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllOtherItemRequests(Long userId, int from, int size) {
        List<ItemRequest> requests = requestRepository.findByRequesterIdNot(
                userId,
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"))
        );
        return getRequestDtoWithItems(requests);
    }

    @Override
    public ItemRequestWithItemsDto getItemRequest(Long userId, Long requestId) {
        ItemRequest itemRequest;
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Пользователя с id " + userId + " не существует");
        }
        if (requestRepository.findById(requestId).isPresent()) {
            itemRequest = requestRepository.findById(requestId).get();
        } else {
            throw new ResourceNotFoundException("Запроса с id " + requestId + " не существует");
        }
        List<Item> items = new ArrayList<>(itemRepository.findByRequestIdOrderById(requestId));
        return ItemRequestMapper.itemRequestToDtoWithItems(itemRequest, ItemMapper.toItemForRequestsDtoList(items));
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        final User user;
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Пользователя с id " + userId + " не существует");
        } else {
            user = userRepository.findById(userId).get();
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequest newItemRequest = requestRepository.save(itemRequest);
        log.info("Новый запрос с id " + newItemRequest.getId() + " успешно создан");
        return ItemRequestMapper.toDto(newItemRequest);
    }

    private List<ItemRequestWithItemsDto> getRequestDtoWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        List<Item> itemsForMapping = itemRepository.getByRequestIdIn(requestIds);
        return requests.stream().map(itemRequest -> ItemRequestMapper.itemRequestToDtoWithItems(
                        itemRequest, getItemsByRequestId(itemRequest.getId(), itemsForMapping)))
                .collect(Collectors.toList());
    }

    private List<ItemForRequestsDto> getItemsByRequestId(Long requestId, List<Item> items) {
        List<Item> itemsToDto = items
                .stream()
                .filter(item -> item.getRequest().getId() == requestId)
                .collect(Collectors.toList());
        return ItemMapper.toItemForRequestsDtoList(itemsToDto);
    }
}
