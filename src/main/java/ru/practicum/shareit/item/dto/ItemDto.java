package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.model.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RequiredArgsConstructor
@Setter
@Getter
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым", groups = Marker.Create.class)
    @Size(max = 255, message = "название не может быть больше 255 символов")
    private String name;
    @NotBlank(message = "Описание не может быть пустым", groups = Marker.Create.class)
    @Size(max = 2048, message = "описание не может быть больше 2048 символов")
    private String description;
    @NotNull(message = "Необходимо указать доступен предмет или нет", groups = Marker.Create.class)
    private Boolean available;
}