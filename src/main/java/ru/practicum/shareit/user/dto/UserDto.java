package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.model.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@Setter
@Getter
public class UserDto {
    private Long id;
    @NotBlank(message = "имя не может быть пустым", groups = Marker.Create.class)
    private String name;
    @NotEmpty(message = "email не может быть пустым", groups = Marker.Create.class)
    @Email(message = "email должен быть валиден", groups = {Marker.Create.class, Marker.Update.class})
    private String email;
}