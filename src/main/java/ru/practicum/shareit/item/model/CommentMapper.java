package ru.practicum.shareit.item.model;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;

import static ru.practicum.shareit.Constants.FORMATTER;

@UtilityClass
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(FORMATTER.format(comment.getCreated()))
                .build();
    }
}
