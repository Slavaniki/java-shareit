package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        final User user = UserMapper.toUser(userDto);
        final User newUser = userRepository.save(user);
        final UserDto newUserDto = UserMapper.toUserDto(newUser);
        log.info("Новый пользователь с id " + userDto.getId() + " создан");
        return newUserDto;
    }

    @Override
    public UserDto getUserById(long userId) {
        final User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id " + userId + " не существует"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("Пользователя с id " + userId + " не существует"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        User newUser = userRepository.save(user);
        log.info("Пользователь с id " + userId + " обновлён");
        return UserMapper.toUserDto(newUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id " + userId + " удалён");
    }
}
