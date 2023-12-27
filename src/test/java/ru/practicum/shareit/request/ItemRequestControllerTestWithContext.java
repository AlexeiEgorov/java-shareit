package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.REQUEST;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTestWithContext {

    private final ObjectMapper mapper;
    @MockBean
    private final ItemRequestService service;
    @MockBean
    private final ItemService itemService;
    private final MockMvc mvc;
    private final ItemRequestDto requestDto;

    @Autowired
    public ItemRequestControllerTestWithContext(ObjectMapper mapper, ItemRequestService service, MockMvc mvc,
                                                ItemService itemService) {
        this.mapper = mapper;
        this.service = service;
        this.mvc = mvc;
        this.itemService = itemService;
        this.requestDto = new ItemRequestDto();
        requestDto.setDescription("Grass highlands and giant white clouds");
    }

    @Test
    void saveNewItemRequest() throws Exception {
        Mockito.when(service.add(anyLong(), any())).thenThrow(new EntityNotFoundException(REQUEST, 1L));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
