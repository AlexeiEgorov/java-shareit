package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER;

@WebMvcTest(controllers = UserController.class)
class ContextUserControllerTest {

    private final ObjectMapper mapper;
    @MockBean
    private final UserService userService;
    private final MockMvc mvc;
    private final UserDto userDto;
    private final UserDto userDto2;
    private final User user;

    @Autowired
    public ContextUserControllerTest(ObjectMapper mapper, UserService userService, MockMvc mvc) {
        this.mapper = mapper;
        this.userService = userService;
        this.mvc = mvc;
        this.userDto = new UserDto();
        userDto.setName("Valentine");
        userDto.setEmail("gregoriansouthwestcom");
        this.userDto2 = new UserDto();
        userDto2.setName("Valentine");
        userDto2.setEmail("gregoriansouthwest@com");
        this.user = new User();
        user.setName("Valentine");
        user.setEmail("gregoriansouthwestcom");
    }

    @Test
    void saveNewUser() throws Exception {

        //mvc.perform(post("/users")
        //                .content(mapper.writeValueAsString(userDto))
        //                .characterEncoding(StandardCharsets.UTF_8)
        //                .contentType(MediaType.APPLICATION_JSON)
        //                .accept(MediaType.APPLICATION_JSON))
        //        .andExpect(status().isBadRequest());

        Mockito.when(userService.save(any())).thenReturn(user);

        //mvc.perform(post("/users")
        //                .content(mapper.writeValueAsString(userDto))
        //                .characterEncoding(StandardCharsets.UTF_8)
        //                .contentType(MediaType.APPLICATION_JSON)
        //                .accept(MediaType.APPLICATION_JSON))
        //        .andExpect(status().isBadRequest());

        Mockito.when(userService.save(any())).thenThrow(new EntityNotFoundException(USER, 1L));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void get() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Valentine");
        userDto.setEmail("gregoriansouthwest@com");
        Mockito.when(userService.get(anyLong())).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Valentine");
        userDto.setEmail("gregoriansouthwest@com");
        Mockito.when(userService.patch(any(), anyLong())).thenReturn(user);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}