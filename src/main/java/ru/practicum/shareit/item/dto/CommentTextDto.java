package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentTextDto {
    @NotBlank(message = "комментарий не может быть пустым")
    @Size(max = 1024, message = "комментарий не может быть больше 1024 символов")
    private String text;
}
