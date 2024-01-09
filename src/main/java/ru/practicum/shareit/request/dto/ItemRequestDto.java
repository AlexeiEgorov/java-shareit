package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
public class ItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 2048, message = "описание не может быть больше 2048 символов")
    private String description;
}
