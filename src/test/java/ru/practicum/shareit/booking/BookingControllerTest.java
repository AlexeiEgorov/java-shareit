package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.Constants.FORMATTER;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookingControllerTest {
    private final BookingController controller;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final User user;
    private final User user2;

    @Autowired
    public BookingControllerTest(BookingController controller, UserRepository userRepository, ItemRepository itemRepository) {
        this.controller = controller;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.user = new User();
        user.setName("Sally");
        user.setEmail("gargantua@woods.sp");
        this.user2 = new User();
        user.setName("Thorin");
        user.setEmail("northernfell@vostok.ru");
    }

    @Test
    void getUserBookings() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        Item item = new Item();
        item.setOwner(savedUser);
        item.setName("Gears");
        item.setAvailable(true);
        Item item2 = new Item();
        item2.setOwner(savedUser2);
        item2.setName("Knives");
        item2.setAvailable(true);
        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, item.getId());
        BookingDto bookingDto2 = new BookingDto(null, start, end, item2.getId());
        ResponseBookingDto resp = controller.add(bookingDto, savedUser2.getId());
        controller.add(bookingDto2, savedUser.getId());
        ResponseBookingDto expected = new ResponseBookingDto(resp.getId(), FORMATTER.format(start),
                FORMATTER.format(end), Status.WAITING);
        expected.setItem(new BookingItemDto(item.getId(), "Gears"));
        expected.setBooker(new BookerDto(user2.getId()));
        assertThat(controller.getUserBookings(user2.getId(), State.ALL, 0, 10), equalTo(List.of(expected)));
        assertThat(controller.getUserBookings(user2.getId(), State.WAITING, 0, 10),
                equalTo(List.of(expected)));
        assertThat(controller.getUserBookings(user2.getId(), State.PAST, 0, 10), equalTo(List.of()));
        assertThat(controller.getUserBookings(user2.getId(), State.CURRENT, 0, 10), equalTo(List.of()));
        assertThat(controller.getUserBookings(user2.getId(), State.FUTURE, 0, 10),
                equalTo(List.of(expected)));
        assertThat(controller.getUserBookings(user2.getId(), State.REJECTED, 0, 10), equalTo(List.of()));
    }

    @Test
    void getUserOwnItemsBookings() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        Item item = new Item();
        item.setOwner(savedUser);
        item.setName("Gears");
        item.setAvailable(true);
        Item item2 = new Item();
        item2.setOwner(savedUser2);
        item2.setName("Knives");
        item2.setAvailable(true);
        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, item.getId());
        BookingDto bookingDto2 = new BookingDto(null, start, end, item2.getId());
        ResponseBookingDto resp = controller.add(bookingDto, savedUser2.getId());
        controller.add(bookingDto2, savedUser.getId());
        ResponseBookingDto expected = new ResponseBookingDto(resp.getId(), FORMATTER.format(start),
                FORMATTER.format(end), Status.APPROVED);
        expected.setItem(new BookingItemDto(item.getId(), "Gears"));
        expected.setBooker(new BookerDto(user2.getId()));
        controller.approve(user.getId(), resp.getId(), true);
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.ALL, 0, 10),
                equalTo(List.of(expected)));
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.WAITING, 0, 10),
                equalTo(List.of()));
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.CURRENT, 0, 10),
                equalTo(List.of()));
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.FUTURE, 0, 10),
                equalTo(List.of(expected)));
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.REJECTED, 0, 10),
                equalTo(List.of()));
        assertThat(controller.getUserOwnItemsBookings(user.getId(), State.PAST, 0, 10),
                equalTo(List.of()));
    }

    @Test
    void getBooking() {
        User savedUser = userRepository.save(user);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(new User());
        Item item = new Item();
        item.setOwner(savedUser);
        item.setName("Gears");
        item.setAvailable(true);
        item = itemRepository.save(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = new BookingDto(null, start, end, item.getId());
        controller.add(bookingDto, savedUser2.getId());
        Assertions.assertThrows(EntityNotFoundException.class, () -> controller.getBooking(user.getId(),
                savedUser3.getId()));
    }
}