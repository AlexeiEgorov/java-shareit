package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Setter
@Getter
public class UserPatchDto {
    private long id;
    private String name;
    @Email(message = "email должен быть валиден")
    private String email;
}