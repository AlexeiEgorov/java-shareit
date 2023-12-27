package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
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

    public ItemServiceImplTest() {
        UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
        BookingRepository bookingRepositoryMock = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepositoryMock = Mockito.mock(CommentRepository.class);
        ItemRequestRepository itemRequestRepositoryMock = Mockito.mock(ItemRequestRepository.class);
        this.mockItemRepository = Mockito.mock(ItemRepository.class);
        itemService = new ItemServiceImpl(mockItemRepository, userRepositoryMock, bookingRepositoryMock,
                commentRepositoryMock, itemRequestRepositoryMock);

        user = new User();
        user.setId(1L);
        user.setName("elliot");
        user.setEmail("go@gmail.com");
        itemRequest = new ItemRequest(1L, "req", user, LocalDateTime.MIN, List.of());

        itemWithoutReq = new Item();
        itemWithoutReq.setOwner(user);
        itemWithoutReq.setName("geyser");
        Mockito
                .when(userRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(user));
        Mockito
                .when(mockItemRepository.save(any(Item.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito
                .when(itemRequestRepositoryMock.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        Mockito
                .when(bookingRepositoryMock.existsFirstByItemIdAndBookerIdAndEndBefore(eq(1L), eq(1L),
                        any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito
                .when(bookingRepositoryMock.existsFirstByItemIdAndBookerIdAndEndBefore(eq(2L), eq(1L),
                        any(LocalDateTime.class)))
                .thenReturn(false);
        Mockito
                .when(commentRepositoryMock.save(any(Comment.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Comment.class));
        Mockito
                .when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemWithoutReq));
    }

    @Test
    void save() {
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
    void patch() {
        final ItemDto itemDtoWithoutReq = new ItemDto();
        itemDtoWithoutReq.setName("geyser");
        final Item itemWithoutReq = new Item();
        itemWithoutReq.setOwner(user);
        itemWithoutReq.setName("geyser");

        assertThrows(EntityNotFoundException.class, () -> itemService.patch(itemDtoWithoutReq, 2L, 1L));
    }

    @Test
    void addComment() {
        CommentTextDto textDto = new CommentTextDto();
        textDto.setText("Papua New-Guinea");
        CommentDto created = itemService.addComment(1L, 1L, textDto);
        CommentDto commentDto = CommentDto.builder()
                .text(textDto.getText())
                .authorName("elliot")
                .created(created.getCreated())
                .build();

        assertThat(created, equalTo(commentDto));
        Assertions.assertThrows(NotAllowedActionException.class, () -> itemService.addComment(1L, 2L,
                textDto));
    }
}