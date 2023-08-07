package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private final User user = new User(1L, "Vasia", "VasiaPupkin@gmail.com");
    private final UserDto userDto = new UserDto(1L, "Vasia", "VasiaPupkin@gmail.com");
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest() {
        Mockito
                .when(userRepository.save(any()))
                .thenReturn(user);
        UserDto user = userService.createUser(userDto);
        assertNotNull(user);
        assertEquals(userDto, user);
    }

    @Test
    void getUserByIdTest() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto user = userService.getUserById(this.user.getId());
        assertEquals(userDto, user);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1000L));
    }

    @Test
    void getAllUsersTest() {
        Mockito
                .when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> users = userService.getAllUsers();
        assertEquals(List.of(userDto), users);
    }

    @Test
    void updateUserTest() {
        UserDto kirchick = new UserDto(user.getId(), "9impulse", "coolFireBeaver@gmail.com");
        User updatedVasia = new User(user.getId(), kirchick.getName(), kirchick.getEmail());
        UserDto updatedVasiaDto = new UserDto(user.getId(), kirchick.getName(), kirchick.getEmail());
        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRepository.save(user))
                .thenReturn(updatedVasia);
        UserDto actualUser = userService.updateUser(user.getId(), kirchick);
        assertEquals(updatedVasiaDto, actualUser);
        Mockito
                .when(userRepository.findById(1000L))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1000L, kirchick));
    }

    @Test
    void updateUserWithNullTest() {
        User yan = new User(2L, "Yan", "Yan@gmail.com");
        UserDto yanDto = new UserDto(2L, "Yan", "Yan@gmail.com");
        UserDto updatedYan = new UserDto(yan.getId(), null, null);
        Mockito
                .when(userRepository.findById(yan.getId()))
                .thenReturn(Optional.of(yan));
        Mockito
                .when(userRepository.save(yan))
                .thenReturn(yan);
        UserDto user = userService.updateUser(yan.getId(), updatedYan);
        assertEquals(yanDto, user);
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(user.getId());
        Mockito
                .verify(userRepository, Mockito.times(1))
                .deleteById(user.getId());
    }
}