package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.CommentDto;

import java.time.format.DateTimeFormatter;

public class CommentMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(formatter.format(comment.getCreated()))
                .build();
    }
}
