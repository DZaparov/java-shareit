package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(User user);

    UserDto updateUser(Long id, User user);

    void deleteUser(Long id);

    UserDto getUserById(Long id);

    List<UserDto> listUsers();
}
