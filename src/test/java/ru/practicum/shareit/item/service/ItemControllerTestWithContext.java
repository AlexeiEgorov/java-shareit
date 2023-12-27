package ru.practicum.shareit.item.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.ITEM;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTestWithContext {

    private final ObjectMapper mapper;
    @MockBean
    private final ItemService itemService;
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mvc;
    private final ItemDto itemDto;

    @Autowired
    public ItemControllerTestWithContext(ObjectMapper mapper, ItemService itemService, MockMvc mvc,
                                         BookingService bookingService) {
        this.mapper = mapper;
        this.itemService = itemService;
        this.mvc = mvc;
        this.bookingService = bookingService;
        this.itemDto = new ItemDto();
        itemDto.setId(5L);
        itemDto.setName("Snake");
        itemDto.setDescription("Yellow");
        itemDto.setAvailable(true);
        User user = new User();
        user.setName("Valentine");
        user.setEmail("gregoriansouthwestcom");
    }

    @Test
    void saveNewItem() throws Exception {
        Mockito.when(itemService.addComment(anyLong(), anyLong(), any())).thenThrow(NotAllowedActionException.class);
        CommentTextDto comment = new CommentTextDto();
        comment.setText("loop-blob-bap-pap");

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        Mockito.when(itemService.save(any(), eq(1L))).thenThrow(new EntityNotFoundException(ITEM, 1L));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());


    }
}
