package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя...");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя " + user.getId() + "...");
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Вызов всех пользователей...");
        return userService.getAllUsers();
    }

    @GetMapping(value = "/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Вызов пользователя по ID:" + id + "...");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя по id:" + userId + "...");
        userService.deleteUser(userId);
        log.info("Пользователь удален");
    }
}
