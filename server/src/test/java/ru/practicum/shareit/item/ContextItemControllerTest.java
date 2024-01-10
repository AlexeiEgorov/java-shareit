package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.ITEM;
import static ru.practicum.shareit.Constants.USER_ID_REQ_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ContextItemControllerTest {

    private final ObjectMapper mapper;
    @MockBean
    private final ItemService service;
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mvc;
    private final ItemDto itemDto;

    @Autowired
    public ContextItemControllerTest(ObjectMapper mapper, ItemService service, MockMvc mvc,
                                         BookingService bookingService) {
        this.mapper = mapper;
        this.service = service;
        this.mvc = mvc;
        this.bookingService = bookingService;
        this.itemDto = new ItemDto();
        itemDto.setId(5L);
        itemDto.setName("Snake");
        itemDto.setDescription("Yellow");
        itemDto.setAvailable(true);
    }

    @Test
    void delete() throws Exception {

        mvc.perform(MockMvcRequestBuilders.delete("/items/1")
                        .header(USER_ID_REQ_HEADER, 1L)
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

        Mockito.when(service.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(MockMvcRequestBuilders.post("/items/2/comment")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .content(mapper.writeValueAsString(text4))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Yohohoho"));

        Mockito.when(service.addComment(anyLong(), anyLong(), any())).thenThrow(NotAllowedActionException.class);
        CommentTextDto comment = new CommentTextDto();
        comment.setText("loop-blob-bap-pap");

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserItems() throws Exception {
        Page<Item> items = Page.empty();
        Mockito.when(service.getUserItems(anyLong(), eq(0), eq(10))).thenReturn(items);

        mvc.perform(get("/items")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", Matchers.hasSize(0))));
    }

    @Test
    void getItem() throws Exception {
        Map<Long, List<Comment>> itemsComments = new HashMap<>();
        User author = new User();
        author.setId(1L);
        author.setName("Sam");
        Comment comment = new Comment(1L, "y", null, author, LocalDateTime.now());
        itemsComments.put(1L, new ArrayList<>());
        itemsComments.get(1L).add(comment);
        Map<Long, List<Booking>> itemsBookings = new HashMap<>();
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                null, author, null);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
                null, author, null);
        itemsBookings.put(1L, new ArrayList<>());
        itemsBookings.get(1L).add(booking2);
        itemsBookings.get(1L).add(booking);

        Item item = new Item();
        item.setId(1L);
        item.setOwner(author);

        Mockito.when(service.getCommentsByItemsIds(anySet()))
                .thenReturn(itemsComments);
        Mockito.when(bookingService.findLastAndNextBookings(anySet()))
                .thenReturn(itemsBookings);
        Mockito.when(service.get(anyLong())).thenReturn(item);

        mvc.perform(get("/items/1")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id").value("1")))
                .andExpect((jsonPath("$.name").isEmpty()))
                .andExpect((jsonPath("$.description").isEmpty()))
                .andExpect((jsonPath("$.available").isEmpty()))
                .andExpect((jsonPath("$.lastBooking.id").value("1")))
                .andExpect((jsonPath("$.nextBooking.id").value("2")))
                .andExpect((jsonPath("$.comments[0].id").value("1")))
                .andExpect((jsonPath("$.comments[0].text").value("y")))
                .andExpect((jsonPath("$.requestId").isEmpty()));
    }

    @Test
    void findByText() throws Exception {
        Page<Item> items = Page.empty();
        Mockito.when(service.findByText(anyLong(), anyString(), eq(0), eq(10)))
                .thenReturn(items);
        mvc.perform(get("/items/search")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .param("text", "Re")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", Matchers.hasSize(0))));

        mvc.perform(get("/items/search")
                        .header(USER_ID_REQ_HEADER, 1L)
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", Matchers.hasSize(0))));
    }

    @Test
    void add() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_REQ_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        Mockito.when(service.save(any(), eq(1L))).thenThrow(new EntityNotFoundException(ITEM, 1L));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(USER_ID_REQ_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {
        Item item = new Item(5L, "Snake", "Yellow", true, null, null);
        Mockito.when(service.patch(any(), eq(1L), eq(1L))).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_REQ_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.id").value("5")))
                .andExpect((jsonPath("$.name").value("Snake")))
                .andExpect((jsonPath("$.description").value("Yellow")))
                .andExpect((jsonPath("$.available").value("true")))
                .andExpect((jsonPath("$.lastBooking").isEmpty()))
                .andExpect((jsonPath("$.nextBooking").isEmpty()))
                .andExpect((jsonPath("$.comments").isEmpty()))
                .andExpect((jsonPath("$.comments").isEmpty()))
                .andExpect((jsonPath("$.requestId").isEmpty()));
    }
}
