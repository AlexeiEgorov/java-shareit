package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentTextDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

class ItemServiceImplTest {
    private final ItemService itemService;
    final User user;
    final ItemRequest itemRequest;
    private final ItemRepository mockItemRepository;
    final Item itemWithoutReq;
    private final ItemRequestRepository itemRequestRepositoryMock;
    private final UserRepository userRepositoryMock;
    private final CommentRepository commentRepositoryMock;
    private final BookingRepository bookingRepositoryMock;

    public ItemServiceImplTest() {
        this.userRepositoryMock = Mockito.mock(UserRepository.class);
        this.bookingRepositoryMock = Mockito.mock(BookingRepository.class);
        this.commentRepositoryMock = Mockito.mock(CommentRepository.class);
        this.itemRequestRepositoryMock = Mockito.mock(ItemRequestRepository.class);
        this.mockItemRepository = Mockito.mock(ItemRepository.class);
        this.itemService = new ItemServiceImpl(mockItemRepository, userRepositoryMock, bookingRepositoryMock,
                commentRepositoryMock, itemRequestRepositoryMock);

        this.user = new User();
        this.user.setId(1L);
        this.user.setName("elliot");
        this.user.setEmail("go@gmail.com");
        this.itemRequest = new ItemRequest(1L, "req", user, LocalDateTime.MIN, List.of());

        this.itemWithoutReq = new Item();
        this.itemWithoutReq.setOwner(user);
        this.itemWithoutReq.setName("geyser");
    }

    @Test
    void save() {
        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito
                .when(itemRequestRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        final ItemDto itemDtoWithReq = new ItemDto();
        itemDtoWithReq.setName("stream");
        itemDtoWithReq.setRequestId(1L);
        final ItemDto itemDtoWithoutReq = new ItemDto();
        itemDtoWithoutReq.setName("geyser");
        final Item itemWithReq = new Item();
        itemWithReq.setOwner(user);
        itemWithReq.setName("stream");
        itemWithReq.setRequest(itemRequest);
        final Item itemWithoutReq = new Item();
        itemWithoutReq.setOwner(user);
        itemWithoutReq.setName("geyser");

        assertThat(itemService.save(itemDtoWithReq, 1L), equalTo(itemWithReq));
        assertThat(itemService.save(itemDtoWithoutReq, 1L), equalTo(itemWithoutReq));
    }

    @Test
    void get() {
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.get(1L));
    }

    @Test
    void getUser() {
        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.getUser(1L));
    }

    @Test
    void getItemRequest() {
        Mockito
                .when(itemRequestRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.getItemRequest(1L));
    }

    @Test
    void patch() {
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemWithoutReq));
        final ItemDto itemDtoWithoutReq = new ItemDto();
        itemDtoWithoutReq.setName("geyser");
        final ItemDto itemDtoWithoutReq2 = new ItemDto();
        itemDtoWithoutReq2.setName("   ");
        itemDtoWithoutReq2.setDescription("   ");
        final ItemDto itemDtoWithoutReq3 = new ItemDto();
        itemDtoWithoutReq3.setName(null);
        itemDtoWithoutReq3.setDescription(null);
        final Item itemWithoutReq = new Item();
        itemWithoutReq.setOwner(user);
        itemWithoutReq.setName("geyser");

        assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDtoWithoutReq, 2L, 1L));
        assertThat(itemService.patch(itemDtoWithoutReq2, 1L, 1L), equalTo(itemWithoutReq));
        assertThat(itemService.patch(itemDtoWithoutReq3, 1L, 1L), equalTo(itemWithoutReq));
    }

    @Test
    void addComment() {
        Mockito
                .when(bookingRepositoryMock.existsFirstByItemIdAndBookerIdAndEndBefore(eq(1L), eq(1L),
                        any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito
                .when(bookingRepositoryMock.existsFirstByItemIdAndBookerIdAndEndBefore(eq(2L), eq(1L),
                        any(LocalDateTime.class)))
                .thenReturn(false);
        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(commentRepositoryMock.save(any(Comment.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Comment.class));
        CommentTextDto textDto = new CommentTextDto();
        textDto.setText("Papua New-Guinea");
        CommentDto created = itemService.addComment(1L, 1L, textDto);
        CommentDto commentDto = CommentDto.builder()
                .text(textDto.getText())
                .authorName("elliot")
                .created(created.getCreated())
                .build();

        assertThat(created, equalTo(commentDto));
        assertThrows(NotAllowedActionException.class, () -> itemService.addComment(1L, 2L,
                textDto));
    }
}