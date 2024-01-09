package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.model.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Setter
@Getter
public class UserDto {
    private Long id;
    @NotBlank(message = "имя не может быть пустым", groups = Marker.Create.class)
    @Size(max = 255, message = "имя не может быть больше 255 символов")
    private String name;
    @NotEmpty(message = "email не может быть пустым", groups = Marker.Create.class)
    @Email(message = "email должен быть валиден", groups = {Marker.Create.class, Marker.Update.class})
    @Size(max = 512, message = "email не может быть больше 512 символов", groups = {Marker.Create.class,
            Marker.Update.class})
    private String email;
}