package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@RequestBody User user) {
        log.info("Попытка создания пользователя {}", user);
        UserDto result = userService.createUser(user);
        log.info("Создан пользователь: {}", result);

        return result;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody User user, @PathVariable Long id) {
        log.info("Попытка обновить пользователя с id={},{}", id, user);
        UserDto result = userService.updateUser(id, user);
        log.info("Обновлен пользователь: {}", result);

        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Попытка удалить пользователя с id={}", id);
        userService.deleteUser(id);

        log.info("Удален пользователь с id={}", id);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("Попытка получить пользователя с id={}", id);
        UserDto result = userService.getUserById(id);
        log.info("Получен пользователь: {}", result);

        return result;
    }

    @GetMapping
    public List<UserDto> listUsers() {
        log.info("Попытка получения всех пользователей");
        List<UserDto> result = userService.listUsers();
        log.info("Получен список пользователей. Количество: {}", result.size());

        return result;
    }
}
