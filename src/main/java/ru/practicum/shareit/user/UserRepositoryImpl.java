package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.*;

@Slf4j
@Component
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User createUser(User user) {
        String email = user.getEmail();
        checkEmail(null, email);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("User with id {} is added to database.", user.getId());
        return user;
    }

    private void checkEmail(User user, String email) {
        if (email == null) {
            throw new ValidationException("email не может быть пустым");
        }
        if (!email.contains("@")) {
            throw new ValidationException("email не содержит @");
        }
        if (user != null) {
            users.values().forEach(u -> {
                if (u.getEmail().equals(email) && !user.getEmail().equals(email)) {
                    throw new DuplicateEmailException(email);
                }
            });
        } else {
            users.values().forEach(u -> {
                if (u.getEmail().equals(email)) {
                    throw new DuplicateEmailException(email);
                }
            });
        }
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            final User oldUser = users.get(user.getId());
            final String email = user.getEmail();
            checkEmail(oldUser, email);
            users.replace(user.getId(), user);
            log.info("User with id {} is updated to database.", user.getId());
        } else {
            throw new SubstanceNotFoundException(String.format("User with id %d isn't exist", user.getId()));
        }
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        if (users.containsKey(userId)) {
            final User userToDelete = getUser(userId);
            users.remove(userToDelete.getId());
            log.info("User with id {} is deleted from database.", userId);
        } else {
            throw new SubstanceNotFoundException(String.format("There isn't user with id %d", userId));
        }
    }

    @Override
    public User getUser(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new SubstanceNotFoundException(String.format("There isn't user with id %d.", userId));
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}