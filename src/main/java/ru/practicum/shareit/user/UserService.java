package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        if (contains(user.getId())) {
            return userStorage.update(user).get();
        }
        log.info("User с id " + user.getId() + " не найден");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public void deleteUser(Integer id) {
        if (contains(id)) {
            userStorage.delete(id);
        } else {
            log.info("User с id " + id + " не найден");
        }
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(Integer id) {
        if (contains(id)) {
            return userStorage.getById(id).get();
        }
        log.info("User с id " + id + " не найден");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private boolean contains(Integer id) {
        return userStorage.getUsersMap().containsKey(id);

    }
}
