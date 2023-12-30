package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class UserServiceImplTest {

    @Test
    void patch() {
        UserRepository mockUserRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(mockUserRepository);
        UserDto userDto = new UserDto();
        userDto.setEmail("g@mail.com");
        userDto.setName("lester");
        UserDto userDto2 = new UserDto();
        userDto2.setEmail("  ");
        userDto2.setName("  ");
        UserDto userDto3 = new UserDto();
        userDto3.setEmail(null);
        userDto3.setName(null);
        User user = new User();
        user.setId(1L);
        user.setName("elliot");
        user.setEmail("go@gmail.com");
        User updated = new User();
        updated.setEmail("g@mail.com");
        updated.setName("lester");
        updated.setId(1L);

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockUserRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, User.class));
        assertThat(userService.patch(userDto, 1L), equalTo(updated));
        assertThat(userService.patch(userDto2, 1L), equalTo(user));
        assertThat(userService.patch(userDto3, 1L), equalTo(user));
    }
}