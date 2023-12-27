package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ConstraintViolationException;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.BOOKING;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTestWithContext {

    private final ObjectMapper mapper;
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mvc;
    private final BookingDto bookingDto;

    @Autowired
    public BookingControllerTestWithContext(ObjectMapper mapper, BookingService bookingService, MockMvc mvc) {
        this.mapper = mapper;
        this.bookingService = bookingService;
        this.mvc = mvc;
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L);
    }

    @Test
    void saveNewBooking() throws Exception {
        Mockito.when(bookingService.getUserBookings(anyLong(), eq(null), anyInt(), anyInt()))
                .thenThrow(ConstraintViolationException.class);

        mvc.perform(get("/bookings")
                        .param("state", "null")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.when(bookingService.add(any(), eq(1L))).thenThrow(ConstraintViolationException.class);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.when(bookingService.add(any(), anyLong())).thenThrow(new EntityNotFoundException(BOOKING, 1L));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}