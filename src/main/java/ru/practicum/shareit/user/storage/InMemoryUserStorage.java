package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.DuplicateEmail;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    @Override
    public User createUser(User user) {
        if (emails.containsKey(user.getEmail())) {
            throw new DuplicateEmail("Пользователь с таким email существует");
        }
        generateUserId(user);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (users.containsKey(id)) {
            emails.remove(users.get(id).getEmail());
            users.remove(id);
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        if (users.containsKey(id)) {
            User userToUpdate = users.get(id);
            if (user.getName() == null) {
                user.setName(userToUpdate.getName());
            }
            if (user.getEmail() == null) {
                user.setEmail(userToUpdate.getEmail());
            }

            if (emails.containsKey(user.getEmail())) { //если такой email есть, то может быть дубликат
                if (!Objects.equals(emails.get(user.getEmail()), id)) { //если id юзера в базе не равен id обновляемого юзера
                    throw new DuplicateEmail("Пользователь с таким email существует");
                }
            }
            emails.remove(userToUpdate.getEmail());

            user.setId(id);
            users.put(id, user);
            emails.put(user.getEmail(), id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    private void generateUserId(User user) {
        if (user.getId() == null) {
            user.setId(++id);
        }
    }
}
