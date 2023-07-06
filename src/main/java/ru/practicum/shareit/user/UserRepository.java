package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User getUser(Long id);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(long userId);
}

