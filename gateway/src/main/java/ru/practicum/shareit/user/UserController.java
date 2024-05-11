package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    public final UserClient userService;

    public UserController(UserClient userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("Попытка создания запроса на создание пользователя {}", user);
        ResponseEntity<Object> result = userService.createUser(user);
        log.info("Создан запрос на создание пользователя: {}", result);

        return result;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user, @PathVariable Long id) {
        log.info("Попытка создания запроса на обновоение пользователя с id={},{}", id, user);
        ResponseEntity<Object> result = userService.updateUser(id, user);
        log.info("Создан запрос на обновление пользователя: {}", result);

        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Попытка создания запроса на удаление пользователя с id={}", id);
        userService.deleteUser(id);

        log.info("Создан запрос на удаление пользователя с id={}", id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Попытка создания запроса на получение пользователя с id={}", id);
        ResponseEntity<Object> result = userService.getUserById(id);
        log.info("Создан запрос на получение пользователя: {}", result);

        return result;
    }

    @GetMapping
    public ResponseEntity<Object> listUsers() {
        log.info("Попытка создания запроса на получение всех пользователей");
        ResponseEntity<Object> result = userService.listUsers();
        log.info("Создан запрос на получение списка пользователей: {}", result);

        return result;
    }
}
