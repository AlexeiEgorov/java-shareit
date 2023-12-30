package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.REQUEST;
import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

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
    void add() throws Exception {
        Mockito.when(service.add(anyLong(), any())).thenThrow(new EntityNotFoundException(REQUEST, 1L));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void get() throws Exception {
        ItemRequest itemRequest = new ItemRequest(1L, requestDto.getDescription(), null, null,
                null);
        Mockito.when(service.get(anyLong(), anyLong())).thenReturn(itemRequest);

        mvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequests() throws Exception {
        ItemRequest itemRequest = new ItemRequest(1L, requestDto.getDescription(), null, null,
                null);
        ItemRequest itemRequest2 = new ItemRequest(2L, requestDto.getDescription(), null, null,
                null);
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(itemRequest);
        requests.add(itemRequest2);
        Mockito.when(service.getUserRequests(anyLong())).thenReturn(requests);

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", hasSize(2))))
                .andExpect((jsonPath("$[0].id").value("1")))
                .andExpect((jsonPath("$[0].description").value(requestDto.getDescription())))
                .andExpect((jsonPath("$[0].items").isEmpty()))
                .andExpect((jsonPath("$[1].id").value("2")))
                .andExpect((jsonPath("$[1].description").value(requestDto.getDescription())))
                .andExpect((jsonPath("$[1].items").isEmpty()));
    }

    @Test
    void getAllNonOwnedRequests() throws Exception {
        Page<ItemRequest> requests = Page.empty();
        Mockito.when(service.getAllNonOwnedRequests(anyLong(), eq(0), eq(10))).thenReturn(requests);

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", hasSize(0))));
    }
}
