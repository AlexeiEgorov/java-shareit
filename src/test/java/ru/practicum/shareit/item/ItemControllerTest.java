package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.InfoBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.Constants.FORMATTER;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemControllerTest {
    private final ItemController controller;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final User user;
    private final User user2;
    private final ItemDto itemDto;
    private final ItemDto itemDto2;

    @Autowired
    public ItemControllerTest(ItemController controller, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.controller = controller;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.user = new User();
        this.user2 = new User();
        this.itemDto = new ItemDto();
        itemDto.setDescription("Org");
        itemDto.setName("go");
        itemDto.setAvailable(true);
        this.itemDto2 = new ItemDto();
        itemDto2.setDescription("117kg");
        itemDto2.setName("Anvil");
        itemDto2.setAvailable(true);
    }

    @Test
    void add() {
        User user = new User();
        long userId = userRepository.save(user).getId();
        ResponseItemDto resp = controller.add(itemDto, userId);
        ResponseItemDto expected = new ResponseItemDto(resp.getId(), "go", "Org", true,
                List.of(), null);
        assertThat(resp, equalTo(expected));
    }

    @Test
    void update() {
        long userId = userRepository.save(user).getId();
        long itemId = controller.add(itemDto, userId).getId();
        ItemDto patch = new ItemDto();
        patch.setName("goro");
        patch.setAvailable(false);
        patch.setDescription("Orda");
        ResponseItemDto resp = new ResponseItemDto(itemId, "goro", "Orda", false, List.of(),
                null);
        assertThat(controller.update(patch, userId, itemId), equalTo(resp));
    }

    @Test
    void getUserItems() {
        long userId = userRepository.save(user).getId();
        User booker = userRepository.save(user2);
        long itemId = controller.add(itemDto, userId).getId();
        long itemId2 = controller.add(itemDto2, userId).getId();
        LocalDateTime bookingStart = LocalDateTime.now().minusDays(2);
        LocalDateTime bookingEnd = LocalDateTime.now().minusDays(1);
        Long bookingId = bookingRepository.save(Booking.builder()
                        .booker(booker)
                        .item(new Item(itemId, "", "", null, null, null))
                        .start(bookingStart)
                        .end(bookingEnd)
                .build()).getId();
        Comment comment = commentRepository.save(Comment.builder()
                .text("Lyrics")
                .author(booker)
                .item(new Item(itemId, "", "", null, null, null))
                .created(LocalDateTime.now())
                .build());

        ResponseItemDto resp = new ResponseItemDto(itemId, "go", "Org", true, new ArrayList<>(),
                null);
        resp.setLastBooking(new InfoBookingDto(bookingId, booker.getId()));
        ResponseItemDto resp2 = new ResponseItemDto(itemId2, "Anvil", "117kg", true, List.of(),
                null);
        resp.getComments().add(CommentDto.builder()
                .authorName(booker.getName())
                .text("Lyrics")
                .id(comment.getId())
                .created(FORMATTER.format(comment.getCreated()))
                .build());
        assertThat(controller.getUserItems(userId, 0, 10), equalTo(List.of(resp, resp2)));
    }

    @Test
    void get() {
        long userId = userRepository.save(user).getId();
        long itemId = controller.add(itemDto, userId).getId();
        ResponseItemDto resp = new ResponseItemDto(itemId, "go", "Org", true, List.of(),
                null);
        assertThat(controller.get(userId, itemId), equalTo(resp));
    }

    @Test
    void findByText() {
        long userId = userRepository.save(user).getId();
        controller.add(itemDto, userId);
        long itemId = controller.add(itemDto2, userId).getId();
        ResponseItemDto resp = new ResponseItemDto(itemId, "Anvil", "117kg", true, List.of(),
                null);
        assertThat(controller.findByText(userId, "nvil", 0, 10), equalTo(List.of(resp)));
        assertThat(controller.findByText(userId, "   ", 0, 10), equalTo(List.of()));
    }
}