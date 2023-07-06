package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(UserDto userDto, Long id);

    public void deleteUser(Long id);

    public List<UserDto> getAllUsers();

    public UserDto getUserById(Long id);
}