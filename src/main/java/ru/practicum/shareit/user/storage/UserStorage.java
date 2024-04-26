package ru.practicum.shareit.user.storage;


import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    void deleteUser(Long id);

    User updateUser(Long id, User user);

    List<User> listUsers();

    User getUserById(Long id);
}
