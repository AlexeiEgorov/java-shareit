package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.model.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Setter
@Getter
public class ItemDto {
    private long id;
    @NotBlank(message = "Имя не может быть пустым", groups = Marker.Create.class)
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = Marker.Create.class)
    private String description;
    @NotNull(message = "Необходимо указать доступен предмет или нет", groups = Marker.Create.class)
    private Boolean available;
}