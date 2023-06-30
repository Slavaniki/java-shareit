package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
@Slf4j
@Component("UserServiceImpl")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(Integer id) {

    }

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }
    /*@Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.add(user);
    }

    @Override
    public User updateUser(User user) {
        if (contains(user.getId())) {
            return userRepository.update(user).get();
        }
        log.info("User с id " + user.getId() + " не найден");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @Override
    public void deleteUser(Integer id) {
        if (contains(id)) {
            userRepository.delete(id);
        } else {
            log.info("User с id " + id + " не найден");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(Integer id) {
        if (contains(id)) {
            return userRepository.getById(id).get();
        }
        log.info("User с id " + id + " не найден");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    private boolean contains(Integer id) {
        return userRepository.getUsersMap().containsKey(id);
    }*/
}
