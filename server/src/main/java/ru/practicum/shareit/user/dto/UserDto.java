package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @NotEmpty
    @Email(message = "Введён некорректный email")
    private String email;
}
