package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.model.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    @NotNull(message = "комментарий не может быть пустым", groups = Marker.Create.class)
    @NotBlank(message = "комментарий не может быть пустым", groups = Marker.Create.class)
    private String text;
    private String authorName;
    private String created;
}
