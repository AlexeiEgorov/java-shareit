package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTestWithContext {

    private final ObjectMapper mapper;
    @MockBean
    private final ItemService service;
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mvc;

    @Autowired
    public ItemControllerTestWithContext(ObjectMapper mapper, ItemService service, MockMvc mvc,
                                         BookingService bookingService) {
        this.mapper = mapper;
        this.service = service;
        this.mvc = mvc;
        this.bookingService = bookingService;
    }

    @Test
    void delete() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentTextDto text = new CommentTextDto();
        CommentTextDto text2 = new CommentTextDto();
        CommentTextDto text3 = new CommentTextDto();
        CommentTextDto text4 = new CommentTextDto();
        CommentDto commentDto = CommentDto.builder()
                .text("Yohohoho")
                .build();
        text.setText(new String(new char[103]).replace("\0", "Yohohohoho"));
        text2.setText("   ");
        text4.setText("Yohohoho");

        mvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(text))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(text2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(text3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.when(service.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(text4))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Yohohoho"));
    }
}
