package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserControllerTest {
    private final UserController controller;
    private final UserDto userDto;
    private final UserDto userDto2;

    @Autowired
    public UserControllerTest(UserController controller) {
        this.controller = controller;
        this.userDto = new UserDto();
        userDto.setName("Eric");
        userDto.setEmail("gym@gym.com");
        this.userDto2 = new UserDto();
        userDto2.setName("Slavic");
        userDto2.setEmail("olda@wine.co");
    }

    @Test
    void add() {
        ResponseUserDto resp = controller.add(userDto);
        ResponseUserDto expected = new ResponseUserDto(resp.getId(), "Eric", "gym@gym.com");
        assertThat(resp, equalTo(expected));
    }

    @Test
    void getAll() {
        ResponseUserDto resp = controller.add(userDto);
        ResponseUserDto resp2 = controller.add(userDto2);
        ResponseUserDto expected = new ResponseUserDto(resp.getId(), "Eric", "gym@gym.com");
        ResponseUserDto expected2 = new ResponseUserDto(resp2.getId(), "Slavic", "olda@wine.co");
        assertThat(controller.getAll(), equalTo(List.of(expected, expected2)));
    }

    @Test
    void delete() {
        ResponseUserDto resp = controller.add(userDto);
        ResponseUserDto resp2 = controller.add(userDto2);
        ResponseUserDto expected = new ResponseUserDto(resp2.getId(), "Slavic", "olda@wine.co");
        controller.delete(resp.getId());
        assertThat(controller.getAll(), equalTo(List.of(expected)));
    }
}