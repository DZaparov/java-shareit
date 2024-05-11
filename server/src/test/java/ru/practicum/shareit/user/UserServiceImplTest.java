package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        UserDto userDto = new UserDto(1L, "Elon", "elon@spacex.com");
        when(userRepository.save(user)).thenReturn(user);

        UserDto createdUserDto = userService.createUser(user);

        assertEquals(userDto, createdUserDto);
        verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    void updateUserWithNullNameTest() {
        User user = new User(1L, null, "elonNEW@spacex.com");
        User currentUser = new User(1L, "Elon", "elon@spacex.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        UserDto updatedUserDto = userService.updateUser(1L, user);

        assertEquals(UserMapper.toUserDto(currentUser), updatedUserDto);
        verify(userRepository, Mockito.times(1)).save(currentUser);
    }

    @Test
    void updateUserWithNullEmailTest() {
        User user = new User(1L, "ElonNew", null);
        User currentUser = new User(1L, "Elon", "elon@spacex.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(currentUser));
        when(userRepository.save(currentUser)).thenReturn(currentUser);

        UserDto updatedUserDto = userService.updateUser(1L, user);

        assertEquals(UserMapper.toUserDto(currentUser), updatedUserDto);
        verify(userRepository, Mockito.times(1)).save(currentUser);
    }

    @Test
    void updateNonexistentUserTest() {
        User user = new User(99L, "ElonNew", "elonNEW@spacex.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(user.getId(), user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(anyLong());
    }

    @Test
    void getUserByIdTest() {
        User user = new User(1L, "Elon", "elon@spacex.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(user.getId());

        assertEquals(UserMapper.toUserDto(user), userDto);
    }

    @Test
    void getUserByNonexistentIdTest() {
        User user = new User(99L, "Elon", "elon@spacex.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void getListUsersTest() {
        User user1 = new User(1L, "Elon", "elon@spacex.com");
        User user2 = new User(2L, "Mark", "Mark@meta.com");

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> currentUsers = userService.listUsers();

        assertEquals(users.stream().map(UserMapper::toUserDto).collect(Collectors.toList()), currentUsers);
    }
}
