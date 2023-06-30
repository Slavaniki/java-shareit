package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Создание пользователя");
        return userService.createUser(user);
    }

    @PatchMapping
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя " + user.getId());
        return userService.updateUser(user);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Вызов пользователя по id:" + id);
        return userService.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer id) {
        log.info("Удаление пользователя по id:" + id);
        userService.deleteUser(id);
        log.info("Пользователь удален");
    }
}
