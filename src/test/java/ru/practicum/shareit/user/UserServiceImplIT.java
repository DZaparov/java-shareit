package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceImplIT {
    @Autowired
    private final UserService service;

    @Test
    void createUserTest() {
        User user = new User(null, "Elon", "elon@spacex.com");

        UserDto expected = UserMapper.toUserDto(user);
        UserDto result = service.createUser(user);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(expected.getName()));
        assertThat(result.getEmail(), equalTo(expected.getEmail()));

    }
}
