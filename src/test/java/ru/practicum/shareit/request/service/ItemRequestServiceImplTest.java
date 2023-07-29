package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    private final User kirchick = new User(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final UserDto kirchickDto = new UserDto(1L, "9impulse", "coolFireBeaver@gmail.com");
    private final User yan = new User(2L, "Yan", "Yan@gmail.com");
    private final UserDto yanDto = new UserDto(2L, "Yan", "Yan@gmail.com");
    private final ItemRequest itemRequest = new ItemRequest(
            3L,
            "MaK",
            kirchick,
            LocalDateTime.of(2022, 10, 20, 15, 55, 0)
    );
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            3L,
            "MaK",
            kirchickDto,
            LocalDateTime.of(2022, 10, 20, 15, 55, 0)
    );
    private final Item item = new Item(
            4L,
            "mouse",
            "cool",
            true,
            yan,
            itemRequest
    );
    private final ItemForRequestsDto itemForRequestsDto = new ItemForRequestsDto(
            item.getId(),
            item.getName(),
            item.getDescription(),
            item.isAvailable(),
            item.getRequest().getId()
    );
    private final ItemRequestWithItemsDto itemRequestWithItemsDto = new ItemRequestWithItemsDto(
            itemRequest.getId(),
            itemRequest.getDescription(),
            itemRequest.getCreated(),
            List.of(itemForRequestsDto)
    );
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItemRequestTest() {
        Mockito
                .when(userRepository.findById(yan.getId()))
                .thenReturn(Optional.of(yan));
        Mockito
                .when(requestRepository.save(any()))
                .thenReturn(itemRequest);
        ItemRequestDto result = itemRequestService.createItemRequest(yan.getId(), itemRequestDto);
        assertEquals(itemRequestDto, result);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.createItemRequest(1000L, itemRequestDto));
    }

    @Test
    void getAllItemsRequestsByUserTest() {
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(requestRepository.findByRequesterIdOrderByCreatedDesc(kirchick.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.getByRequestIdIn(anyList()))
                .thenReturn(List.of(item));
        List<ItemRequestWithItemsDto> result = itemRequestService.getAllItemsRequestsByUser(kirchick.getId());
        assertEquals(List.of(itemRequestWithItemsDto), result);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getAllItemsRequestsByUser(1000L));
    }

    @Test
    void getAllOtherItemRequestsTest() {
        Mockito
                .when(requestRepository.findByRequesterIdNot(
                                yan.getId(),
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                        )
                )
                .thenReturn(List.of(itemRequest));
        Mockito
                .when(itemRepository.getByRequestIdIn(anyList()))
                .thenReturn(List.of(item));
        List<ItemRequestWithItemsDto> result = itemRequestService.getAllOtherItemRequests(yan.getId(), 0, 1);
        assertEquals(List.of(itemRequestWithItemsDto), result);
    }

    @Test
    void getItemRequestTest() {
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(requestRepository.findById(itemRequest.getId()))
                .thenReturn(Optional.of(itemRequest));
        Mockito
                .when(itemRepository.findByRequestIdOrderById(itemRequest.getId()))
                .thenReturn(List.of(item));
        ItemRequestWithItemsDto result = itemRequestService.getItemRequest(kirchick.getId(), itemRequest.getId());
        assertEquals(itemRequestWithItemsDto, result);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getItemRequest(1000L, itemRequest.getId()));
        Mockito
                .when(userRepository.findById(kirchick.getId()))
                .thenReturn(Optional.of(kirchick));
        Mockito
                .when(requestRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> itemRequestService.getItemRequest(kirchick.getId(), 1000L));
    }
}
