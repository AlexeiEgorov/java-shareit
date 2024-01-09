package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;


public class BookingServiceImplTest {
    private final BookingService service;
    private final ItemRepository mockItemRepository;
    private final UserRepository userRepositoryMock;
    private final BookingRepository bookingRepositoryMock;

    public BookingServiceImplTest() {
        this.mockItemRepository = mock(ItemRepository.class);
        this.userRepositoryMock = mock(UserRepository.class);
        this.bookingRepositoryMock = mock(BookingRepository.class);
        this.service = new BookingServiceImpl(bookingRepositoryMock, mockItemRepository, userRepositoryMock);
    }

    @Test
    void get() {
        Booking booking = new Booking();
        Mockito
                .when(bookingRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(booking));
        assertThat(service.get(1L), equalTo(booking));

        Mockito
                .when(bookingRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.get(1L));
    }

    @Test
    void getUser() {
        User user = new User();
        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(user));
        assertThat(service.getUser(1L), equalTo(user));

        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getUser(1L));
    }

    @Test
    void getItem() {
        Item item = new Item();
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        assertThat(service.getItem(1L), equalTo(item));

        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getItem(1L));
    }

}