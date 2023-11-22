package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAllowedActionException;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.practicum.shareit.Constants.ITEM;
import static ru.practicum.shareit.Constants.USER;
import static ru.practicum.shareit.item.model.CommentMapper.toCommentDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Item save(Item item, Long ownerId) {
        User owner = getUser(ownerId);
        item.setOwner(owner);
        return repository.save(item);
    }

    @Override
    @Transactional
    public Item patch(ItemDto patch, Long ownerId, Long id) {
        Item item = get(id);
        if (!(item.getOwner().getId().equals(ownerId))) {
            throw new EntityNotFoundException(ITEM, id);
        }
        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null && !patch.getDescription().isBlank()) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        return repository.save(item);
    }

    @Override
    public Collection<Item> getUserItems(Long ownerId) {
        getUser(ownerId);
        return repository.findAllByOwnerId(ownerId);
    }

    @Override
    public Item get(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(ITEM, id));
    }

    @Override
    @Transactional
    public void delete(Long ownerId, Long id) {
        repository.deleteById(id);
    }

    @Override
    public Collection<Item> findByText(Long userId, String text) {
        getUser(userId);
        return repository.findItemsByText("%" + text.toUpperCase() + "%");
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER, userId));
    }

    @Override
    public Collection<BookingItemDto> findBookingsByIdIn(Set<Long> itemsIds) {
        return repository.findAllByIdIn(itemsIds);
    }

    @Override
    public Collection<BookingItemDto> findBookingItemsByOwner(Long ownerId) {
        getUser(ownerId);
        return repository.findAlLByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .orElseThrow(() -> new NotAllowedActionException("Чтобы оставить комментарий, необходимо " +
                        "завершить аренду предмета.", itemId.toString()));
        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .item(booking.getItem())
                .author(booking.getBooker())
                .created(LocalDateTime.now())
                .build();
        CommentDto created = toCommentDto(commentRepository.save(comment));
        created.setAuthorName(comment.getAuthor().getName());
        return created;
    }

    @Override
    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }
}
