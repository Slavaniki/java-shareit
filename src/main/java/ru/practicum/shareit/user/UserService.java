package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    public User createUser(User user);

    public User updateUser(User user);

    public void deleteUser(Integer id);

    public Collection<User> getAllUsers();

    public User getUserById(Integer id);
}