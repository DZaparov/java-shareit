package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(User user) {
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(Long id, User user) {
        return UserMapper.toUserDto(userStorage.updateUser(id, user));
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public List<UserDto> listUsers() {
        return userStorage.listUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
